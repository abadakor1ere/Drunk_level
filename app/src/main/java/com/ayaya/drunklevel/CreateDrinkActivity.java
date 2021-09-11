package com.ayaya.drunklevel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class CreateDrinkActivity extends AppCompatActivity {
    
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
    }
}
