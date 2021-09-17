package com.ayaya.drunklevel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class QueryWeight extends AppCompatActivity {
    
    private final List<String> unitsList = Arrays.asList(new String[]{"kg","oz"});
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_weight);
    
        Spinner weightUnitSpinner = findViewById(R.id.weight_unit);
        weightUnitSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, unitsList));
    
        findViewById(R.id.validate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float weight = Float.parseFloat(((EditText)findViewById(R.id.weight)).getText().toString());
                if (unitsList.get(((Spinner)findViewById(R.id.weight_unit)).getSelectedItemPosition()).equals("oz"))
                    weight /= 35.274f;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("weight", weight);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
    
    
    
}
