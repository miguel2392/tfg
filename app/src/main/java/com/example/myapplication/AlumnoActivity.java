package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AlumnoActivity extends AppCompatActivity {

    private static String EXTRA_NAME = "EXTRA_NAME";
    private String nombreAlumno;
    private int nota;

    public static void startActivity(Context context, String name) {

        Intent intent = new Intent(context, AlumnoActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        context.startActivity(intent);
    }

    private EditText et1;
    private String idAlumno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno);

        nombreAlumno = getIntent().getStringExtra(EXTRA_NAME);

        et1 = findViewById(R.id.editTextNota);
        Button subirNota = findViewById(R.id.subirnota);

        subirNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // método para crear documentos en la base de datos
                nota = Integer.parseInt(et1.getText().toString().trim());
                subirNota(nota,nombreAlumno);
            }
        });

    }

    private void subirNota(int nota, String name){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        idAlumno = user.getUid();

        Map<String, Object> calificacion = new HashMap<>();
        calificacion.put("nombre_alumno", name);
        calificacion.put("calificacion", nota);
        //calificacion.put("calificacion", nota2);

        db.collection("presentaciones").document("ktgRN7xEDuiFzcIjNrc0")
                .collection("Calificaciones").document(idAlumno).set(calificacion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("¡¡¡", "DocumentSnapshot successfully written!");
                        // TODO Mandar por ble trama adverstising con idAsignatura y idPresentacion.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("¡¡¡", "Error writing document", e);
                    }
                });

    }


    }

