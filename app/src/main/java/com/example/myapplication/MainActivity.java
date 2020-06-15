package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Esta es la primera actividad que se ejecuta en la aplicacion. Esta activdidad se encarga del routing: devcide:
 *  -Si el ususario no esta logueado, se lanza la {@link AuthenticationActivity}
 *  -Si el ususario esta logueado como profesor se lanza la {@link ProfesorActivity}
 *  -Si el ususario esta logueado como alumno se lanza la {@link AlumnoScanActivity}
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AppDatabaseManager appDatabaseManager = new AppDatabaseManager();
        setContentView(R.layout.activity_main);

        final String uuid = appDatabaseManager.getUserId();

        if (uuid == null) {
            AuthenticationActivity.startActivity(this);
            finish();
            return;
        }

        appDatabaseManager.getProfessor(uuid, new AppDatabaseManager.ProfessorListener() {
            @Override
            public void onProfessorReceived(String name) {
                if (name != null) {
                    ProfesorActivity.startActivity(MainActivity.this, name);
                    finish();
                } else {
                    appDatabaseManager.getAlumno(uuid, new AppDatabaseManager.AlumnoListener() {
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

