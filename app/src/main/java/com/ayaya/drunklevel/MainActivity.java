package com.ayaya.drunklevel;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    SharedPreferences save;
    List<Conso> consos = new ArrayList<>();
    
    ActivityResultLauncher<Object> getConso;
    
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
        
        // création d'un moyen de lancer les activités permettant la demande de ce qui a été consommé
        getConso = registerForActivityResult(new ActivityResultContract<Object, Conso>() {
            @Override
            public Intent createIntent(Context context, Object input) {
                return new Intent(context, SelectDrinkActivity.class);
            }
            @Override
            public Conso parseResult(int resultCode, Intent intent) {
                if (resultCode == RESULT_OK && intent.getFloatExtra("volume", -1f) != -1f && intent.getStringExtra("drink") != null)
                    return new Conso(new Date().getTime(), Drink.fromJSON(intent.getStringExtra("drink")), intent.getFloatExtra("volume", -1f));
                return null;
            }
        }, new ActivityResultCallback<Conso>() {
            @Override
            public void onActivityResult(Conso conso) {
                if (conso == null)
                    return;
                consos.add(conso);
                Toast.makeText(getApplicationContext(), conso.getTime()+" "+conso.getVolume()+"L de "+conso.getDrink().getName(), Toast.LENGTH_LONG).show();
            }
        });
        
        // Si le sexe ou le poids n'est pas dans la sauvegarde, on les demande
        if (save.getInt("sex", -1) == -1 || save.getFloat("weight", -1f) == -1f) {
            Intent sexIntent = new Intent(getApplicationContext(), QuerySex.class);
            startActivity(sexIntent);
        }
        
        // bouton d'ajout de consommation
        findViewById(R.id.add_conso).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getConso.launch(null);
            }
        });
    }
}