package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.calificaciones.CalificacionActivity;


/**
 * En esta actividad un profesor introduce el nombre de la nueva presentación a crear.
 */
public class PresentacionActivity extends AppCompatActivity {

    public static void startActivity(Context context, String asignaturaID, String nombreAsignatura) {

        Intent intentPresentacionActivity = new Intent(context, PresentacionActivity.class);
        intentPresentacionActivity
                .putExtra("Id asignatura", asignaturaID)
                .putExtra("nombre",nombreAsignatura);;

        context.startActivity(intentPresentacionActivity);
    }

    private String idAsignatura;
    private String name;
    private EditText et1;
    private String nombrePresentacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        recibirDatos();
        setTitle(name);

        et1 = findViewById(R.id.editTextTitulo);


        Button empezarPresentacion = findViewById(R.id.buttonEmpezarPresentacion);
        empezarPresentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // método para crear documentos en la base de datos
                nombrePresentacion = et1.getText().toString();
                createDbDocument();
            }
        });
    }

    private void recibirDatos(){
        Bundle idRecibido = getIntent().getExtras();
        idAsignatura = idRecibido.getString("Id asignatura");
        name = idRecibido.getString("nombre");
        Log.d("!!!", idAsignatura);
    }

    private void createDbDocument(){
        AppDatabaseManager appDatabaseManager = new AppDatabaseManager();
        appDatabaseManager.createPresentacion(name, idAsignatura, nombrePresentacion, new AppDatabaseManager.PresentacionCreationListener() {
            @Override
            public void onFinish(String generatedId) {
                if (generatedId != null) {
                    CalificacionActivity.startActivity(PresentacionActivity.this, idAsignatura, generatedId, nombrePresentacion);
                    finish();
                } else {
                    Toast.makeText(PresentacionActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
