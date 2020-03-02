package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openActivityA (View view){
        Intent intentA = new Intent(this, ActivityA.class);
        startActivity(intentA);
    }

    public void openActivityB (View view){
        Intent intentB = new Intent(this, ActivityB.class);
        startActivity(intentB);

    }
}
