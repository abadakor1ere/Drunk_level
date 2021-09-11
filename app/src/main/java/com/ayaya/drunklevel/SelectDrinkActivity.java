package com.ayaya.drunklevel;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectDrinkActivity extends AppCompatActivity {
    
    private Drink selectedDrink = null;
    private ActivityResultLauncher<Object> getVolume;
    private ActivityResultLauncher<Object> createDrink;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_drink);
        
        SharedPreferences save = getSharedPreferences("save", MODE_PRIVATE);
        final List<Drink> drinks = new ArrayList<>();
        try {
            JSONArray jsonDrinks = new JSONArray(save.getString("drinks", "[]"));
            for (int i = 0; i < jsonDrinks.length(); i++) {
                JSONObject jsonDrink = jsonDrinks.getJSONObject(i);
                drinks.add(new Drink(jsonDrink.getString("name"), jsonDrink.getLong("concentration"), jsonDrink.getString("imageName")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        drinks.add(new Drink("Piña colada", 126, getResources().getResourceName(R.drawable.liquor_svg)));
        drinks.add(new Drink("Jager", 276, getResources().getResourceName(R.drawable.herbal_liquor_svg)));
        drinks.add(new Drink("Whisky", 316, getResources().getResourceName(R.drawable.whiskey_svg)));
        drinks.add(new Drink("Bière", 67, getResources().getResourceName(R.drawable.beer_svg)));
        drinks.add(new Drink("Mojito", 95, getResources().getResourceName(R.drawable.mojito_svg)));
        GridView drinksGrid = findViewById(R.id.drinks);
        drinksGrid.setAdapter(new DrinksAdapter(getApplicationContext(), drinks));
        
        drinksGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0) {
                    createDrink.launch(null);
                } else {
                    selectedDrink = drinks.get(i-1);
                    getVolume.launch(null);
                }
            }
        });
    
        // création d'un moyen de lancer l'activité permettant la demande d'un volume
        getVolume = registerForActivityResult(new ActivityResultContract<Object, Float>() {
            @Override
            public Intent createIntent(Context context, Object input) {
                return new Intent(context, SelectVolumeActivity.class);
            }
            @Override
            public Float parseResult(int resultCode, Intent intent) {
                if (resultCode == RESULT_OK && intent.getFloatExtra("volume", -1f) != -1f)
                    return intent.getFloatExtra("volume", -1f);
                return null;
            }
        }, new ActivityResultCallback<Float>() {
            @Override
            public void onActivityResult(Float volume) {
                if (volume == null || selectedDrink == null)
                    return;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("volume", volume);
                resultIntent.putExtra("drink", selectedDrink.toJSON().toString());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    
        // création d'un moyen de lancer l'activité permettant la création d'une nouvelle boisson et de la retourner
        createDrink = registerForActivityResult(new ActivityResultContract<Object, Drink>() {
            @Override
            public Intent createIntent(@NonNull Context context, Object input) {
                return new Intent(context, CreateDrinkActivity.class);
            }
            @Override
            public Drink parseResult(int resultCode, @Nullable Intent intent) {
                if (resultCode == RESULT_OK && intent.getStringExtra("drink") != null)
                    return Drink.fromJSON(intent.getStringExtra("drink"));
                return null;
            }
        }, new ActivityResultCallback<Drink>(){
            @Override
            public void onActivityResult(Drink result) {
                selectedDrink = result;
                if (selectedDrink!=null)
                    getVolume.launch(null);
            }
        });
    }
    
    public class DrinksAdapter extends BaseAdapter {
        
        private Context context;
        private List<Drink> drinks;
        private LayoutInflater inflater;
        
        public DrinksAdapter(Context context, List<Drink> drinks) {
            this.context = context;
            this.drinks = drinks;
            inflater = LayoutInflater.from(context);
        }
        
        @Override
        public int getCount() {
            return drinks.size() + 1;
        }
    
        @Override
        public Object getItem(int i) {
            return null;
        }
    
        @Override
        public long getItemId(int i) {
            return 0;
        }
    
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = inflater.inflate(R.layout.drink_view, viewGroup, false);
            String name = i==0 ? getString(R.string.add_new_drink) : (drinks.get(i-1).getName()+" ("+(int)drinks.get(i-1).getDegree()+"%)");
            ((ImageView)view.findViewById(R.id.icon)).setImageResource(i==0?R.drawable.ic_plus:getResources().getIdentifier(drinks.get(i-1).getImageName(), "drawable", getPackageName()));
            ((TextView)view.findViewById(R.id.name)).setText(name);
            return view;
        }
    }
    
}
