package com.ayaya.drunklevel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

public class SelectVolumeActivity extends AppCompatActivity {
    
    private final List<String> unitsList = Arrays.asList(new String[]{"cL","mL"});
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_glass);
    
        Spinner volumeUnitSpinner = findViewById(R.id.volume_unit);
        volumeUnitSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, unitsList));
    
        findViewById(R.id.validate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float volume = Float.parseFloat(((EditText)findViewById(R.id.volume)).getText().toString());
                switch (unitsList.get(((Spinner)findViewById(R.id.volume_unit)).getSelectedItemPosition())) {
                    case "mL":
                        volume /= 10f;
                    case "cL":
                        volume /= 100f;
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("volume", volume);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
