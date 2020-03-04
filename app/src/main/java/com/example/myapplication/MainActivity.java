package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // Initialize Firebase Auth


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            openAuthenticationActivity(null);
            finish();
        }

    }

    public void openActivityA (View view){
        Intent intentA = new Intent(this, ActivityA.class);
        startActivity(intentA);
    }

    public void openActivityB (View view){
        Intent intentB = new Intent(this, ActivityB.class);
        startActivity(intentB);

    }

    public void signOut (View view){
        FirebaseAuth.getInstance().signOut();
        openAuthenticationActivity(null);
        finish();
    }

    public void openAuthenticationActivity (View view){
        Intent intentSignIn = new Intent(this, AuthenticationActivity.class);
        startActivity(intentSignIn);

    }
}
