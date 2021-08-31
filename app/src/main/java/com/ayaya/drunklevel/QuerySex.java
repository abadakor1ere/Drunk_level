package com.ayaya.drunklevel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class QuerySex extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_sex);
        
        findViewById(R.id.female).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {setSex(Sex.FEMALE);
            }
        });
        findViewById(R.id.male).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSex(Sex.MALE);
            }
        });
    }
    
    protected void setSex(int sex) {
        SharedPreferences save = getSharedPreferences("save", Activity.MODE_PRIVATE);
        save.edit().putInt("sex", sex).commit();
        if (save.getFloat("weight", -1f) == -1f) {
            Intent i = new Intent(this, QueryWeight.class);
            startActivity(i);
        }
        finish();
    }
    
}
