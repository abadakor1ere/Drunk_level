package com.ayaya.drunklevel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateDrinkActivity extends AppCompatActivity {
    
    private static final List<String> iconsNames = Arrays.asList("absinthe_svg","b52_svg","beer_svg","bloody_mary_svg","brandy_svg",
            "champagne_svg","cocktail_1_svg","cocktail_2_svg","cocktail_3_svg","cocktail_4_svg","cocktail_5_svg","cocktail_6_svg","cocktail_7_svg",
            "cocktail_8_svg","cocktail_shaker_svg","cocktail_svg","cosmopolitan_svg","cuba_libre_svg","gin_svg","gin_tonic_svg","herbal_liquor_svg",
            "liquor_1_svg","liquor_svg","mai_thai_svg","margarita_svg","martini_1_svg","martini_svg","mojito_svg","old_fashion_svg","rum_svg",
            "sangria_svg","soda_1_svg","soda_svg","tequila_sunrise_svg","vodka_1_svg","vodka_svg","whiskey_1_svg","whiskey_svg");
    private String iconName = "liquor_1_svg";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_drink);
        
        findViewById(R.id.validate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Vérification des entrées
                EditText name = findViewById(R.id.name);
                EditText alcohol = findViewById(R.id.alcohol_degree);
                ImageView icon = findViewById(R.id.icon);
                if (name.getText().toString().equals(""))
                    name.setError(getResources().getString(R.string.required));
                else if (alcohol.getText().toString().equals(""))
                    alcohol.setError(getResources().getString(R.string.required));
                else {
                    // renvoi de la nouvelle boisson
                    Drink drink = new Drink(name.getText().toString(), Drink.degreeToConcentration(Float.parseFloat(alcohol.getText().toString())), iconName);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("drink", drink.toJSON().toString());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
        
        final List<Integer> iconsRessouces = new ArrayList<>();
        for (String iconName : iconsNames)
            iconsRessouces.add(getResources().getIdentifier(iconName, "drawable", getPackageName()));
        GridView iconsGrid = findViewById(R.id.icons);
        iconsGrid.setAdapter(new ImagesAdapter(this, iconsRessouces));
        iconsGrid.setColumnWidth(128);
        iconsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                iconName = iconsNames.get(i);
                ((ImageView)findViewById(R.id.icon)).setImageResource(iconsRessouces.get(i));
            }
        });
        
        findViewById(R.id.icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //View iconsView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.icon_selector, (LinearLayout)findViewById(android.R.id.content));
                //new AlertDialog.Builder(getApplicationContext()).setView(iconsView).create().show();
                if (findViewById(R.id.icons).getVisibility() == View.VISIBLE)
                    findViewById(R.id.icons).setVisibility(View.GONE);
                else findViewById(R.id.icons).setVisibility(View.VISIBLE);
            }
        });
    }
    
    
    public class ImagesAdapter extends BaseAdapter {
        
        private Context context;
        private List<Integer> imagesResources;
        
        public ImagesAdapter(Context context, List<Integer> imagesResources) {
            this.context = context;
            this.imagesResources = imagesResources;
        }
        
        @Override
        public int getCount() {
            return imagesResources.size();
        }
        
        @Override
        public Integer getItem(int i) {
            return imagesResources.get(i);
        }
        
        @Override
        public long getItemId(int i) {
            return 0;
        }
        
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(128,128));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(imagesResources.get(i));
            return imageView;
        }
    }
    
}
