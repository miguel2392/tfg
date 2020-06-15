package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Esta es la primera actividad que se ejecuta en la aplicacion. Esta activdidad se encarga del routing: devcide:
 *  -Si el ususario no esta logueado, se lanza la {@link AuthenticationActivity}
 *  -Si el ususario esta logueado como profesor se lanza la {@link ProfesorActivity}
 *  -Si el ususario esta logueado como alumno se lanza la {@link AlumnoScanActivity}
 */
public class MainActivity extends AppCompatActivity {

    public static void startActivity(Context context) {
        Intent intentMain = new Intent(context, MainActivity.class);
        context.startActivity(intentMain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AppAuthManager appAuthManager = new AppAuthManager();
        final AppDatabaseManager appDatabaseManager = new AppDatabaseManager();
        setContentView(R.layout.activity_main);

        final String uuid = appAuthManager.getUserId();

        if (uuid == null) {
            AuthenticationActivity.startActivity(this);
            finish();
            return;
        }

        appDatabaseManager.getProfessor(new AppDatabaseManager.ProfessorListener() {
            @Override
            public void onProfessorReceived(String name) {
                if (name != null) {
                    ProfesorActivity.startActivity(MainActivity.this, name);
                    finish();
                } else {
                    appDatabaseManager.getAlumno(new AppDatabaseManager.AlumnoListener() {
                        @Override
                        public void onAlumnoReceived(String name) {
                            if (name != null) {
                                AlumnoScanActivity.startActivity(MainActivity.this, name);
                                finish();
                            } else {
                                throw new IllegalStateException();
                            }
                        }
                    });
                }
            }
        });
    }
}

