package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class PresentacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        recibirDatos();

    }

    private void recibirDatos(){
        Bundle idRecibido = getIntent().getExtras();
        String idAsignatura = idRecibido.getString("Id asignatura");
        Log.d("!!!", idAsignatura);
    }
}
