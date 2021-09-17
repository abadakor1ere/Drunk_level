package com.ayaya.drunklevel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    
    private SharedPreferences save;
    private List<Conso> consos = new ArrayList<>();
    private int sex;
    private float weight;
    
    ActivityResultLauncher<Object> addConso;
    ActivityResultLauncher<Object> setSex;
    ActivityResultLauncher<Object> setWeight;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // récupération de la sauvegarde et de l'historique des consommations
        save = getSharedPreferences("save", MODE_PRIVATE);
        try {
            JSONArray jsonConsos = new JSONArray(save.getString("consos", "[]"));
            for (int i = 0; i < jsonConsos.length(); i++)
                consos.add(Conso.fromJSON(jsonConsos.getJSONObject(i)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // création d'un moyen de modifier le sexe et le poids
        setSex = registerForActivityResult(new ActivityResultContract<Object, Integer>() {
            @Override
            public Intent createIntent(Context context, Object input) {
                return new Intent(context, QuerySex.class);
            }
            @Override
            public Integer parseResult(int resultCode, Intent intent) {
                if (resultCode == RESULT_OK && intent.getIntExtra("sex", -1) != -1)
                    return intent.getIntExtra("sex", -1);
                return null;
            }
        }, new ActivityResultCallback<Integer>() {
            @Override
            public void onActivityResult(Integer result) {
                sex = result;
                save.edit().putInt("sex", result).apply();
            }
        });
        setWeight = registerForActivityResult(new ActivityResultContract<Object, Float>() {
            @Override
            public Intent createIntent(Context context, Object input) {
                return new Intent(context, QueryWeight.class);
            }
            @Override
            public Float parseResult(int resultCode, Intent intent) {
                if (resultCode == RESULT_OK && intent.getFloatExtra("weight", -1f) != -1f)
                    return intent.getFloatExtra("weight", -1f);
                return null;
            }
        }, new ActivityResultCallback<Float>() {
            @Override
            public void onActivityResult(Float result) {
                weight = result;
                save.edit().putFloat("weight", result).apply();
            }
        });
        
        // création d'un moyen de lancer les activités permettant la demande de ce qui a été consommé
        addConso = registerForActivityResult(new ActivityResultContract<Object, Conso>() {
            @Override
            public Intent createIntent(Context context, Object input) {
                return new Intent(context, SelectDrinkActivity.class);
            }
            @Override
            public Conso parseResult(int resultCode, Intent intent) {
                if (resultCode == RESULT_OK && intent.getFloatExtra("volume", -1f) != -1f && intent.getStringExtra("drink") != null)
                    return new Conso(new Date().getTime()/1000, Drink.fromJSON(intent.getStringExtra("drink")), intent.getFloatExtra("volume", -1f));
                return null;
            }
        }, new ActivityResultCallback<Conso>() {
            @Override
            public void onActivityResult(Conso conso) {
                if (conso == null)
                    return;
                consos.add(conso);
                refreshDrunkLevel();
            }
        });
        
        // Si le sexe ou le poids n'est pas dans la sauvegarde, on les demande
        if (save.getFloat("weight", -1f) == -1f)
            setWeight.launch(null);
        else
            weight = save.getFloat("weight", -1f);
        if (save.getInt("sex", -1) == -1)
            setSex.launch(null);
        else
            sex = save.getInt("sex", -1);
        
        // bouton d'ajout de consommation
        findViewById(R.id.add_conso).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addConso.launch(null);
            }
        });

        refreshDrunkLevel();
    }

    protected void refreshDrunkLevel() {
        Toast.makeText(this, consos.toString(), Toast.LENGTH_SHORT).show();
        TextView tauxAlcoolemie = findViewById(R.id.taux_alcoolemie);
        tauxAlcoolemie.setText("Voici votre taux :"+getBloodConcentration(consos, new Date().getTime()/1000, weight, sex)+"g/L");

    }

    /**
     * Calcule la concentration massique d'alcool dans le sang au temps time
     * @param time en secondes
     * @param mass en kg
     * @param consos doit être triée chronologiquement
     * @see Conso
     * @see Sex
     * @return concentration en g/L
     */
    public static float getBloodConcentration(List<Conso> consos, long time, float mass, int sex) {
        float coeffDiffusion = sex==Sex.MALE ? 0.7f : sex==Sex.FEMALE ? 0.6f : 0.65f;
        float concentration = 0;
        for (int i = 0; i < consos.size(); i++) {
            if (consos.get(i).getTime() > time)
                break;
            float concentrationConso = consos.get(i).getAlcoholMass() / (mass * coeffDiffusion);
            concentration += concentrationConso;
            long pauseTime = (i==consos.size()-1?time:consos.get(i+1).getTime()) - consos.get(i).getTime();
            concentration = Math.max(0, concentration - pauseTime * 0.12f / 3600); // taux de dégradation moyen de l'alcool de 12 g/L/h
        }
        return concentration;
    }
    
    public static Map<Long,Float> getBloodConcentrations(List<Conso> consos, long fromTime, long toTime, float mass, int sex) {
        Map<Long,Float> concentrations = new HashMap<>();
        float coeffDiffusion = sex==Sex.MALE ? 0.7f : sex==Sex.FEMALE ? 0.6f : 0.65f;
        float concentration = 0;
        for (int i = 0; i < consos.size(); i++) {
            // Ajout de l'alcool consommé
            float concentrationConso = consos.get(i).getAlcoholMass() / (mass * coeffDiffusion);
            concentration += concentrationConso;
            // Si c'est dans l'intervale
            if  (fromTime <= consos.get(i).getTime() && consos.get(i).getTime() <= toTime) {
                concentrations.put(consos.get(i).getTime(), concentration);
            }
            // Si le suivant est le premier dans l'intervale
            if (concentrations.isEmpty() && i+1<consos.size() && consos.get(i+1).getTime() >= fromTime) {
                long pauseTime = fromTime - consos.get(i).getTime();
                concentrations.put(fromTime, Math.max(0, concentration - pauseTime * 0.12f / 3600));
            }
            // Si c'est le dernier dans l'intervale
            if (i+1>=consos.size() || consos.get(i+1).getTime() >= toTime) {
                long pauseTime = toTime - consos.get(i).getTime();
                concentrations.put(toTime, Math.max(0, concentration - pauseTime * 0.12f / 3600));
                break;
            }
            // Dégradation de l'alcool jusqu'à la prochaine consommation
            long pauseTime = (i+1==consos.size()?toTime:consos.get(i+1).getTime()) - consos.get(i).getTime();
            concentration = Math.max(0, concentration - pauseTime * 0.12f / 3600); // taux de dégradation moyen de l'alcool de 12 g/L/h
        }
        if (concentrations.get(fromTime)==null)
            concentrations.put(fromTime, 0f);
        if (concentrations.get(toTime)==null)
            concentrations.put(toTime, 0f);
        return concentrations;
    }
    
}