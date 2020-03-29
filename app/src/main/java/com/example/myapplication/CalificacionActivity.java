package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CalificacionActivity extends AppCompatActivity {

    private String idAsignatura;
    private String idPresentacion;
    private String nombrePresentacion;
    private int media;

    public static void startActivity(Context context, String asignaturaID, String presentacionID, String nombrePresentacion) {

        Intent intentCalificacionActivity = new Intent(context, CalificacionActivity.class);
        intentCalificacionActivity
                .putExtra("Id_asignatura", asignaturaID)
                .putExtra("Id_presentacion", presentacionID)
                .putExtra("Nombre_presentacion", nombrePresentacion);


        context.startActivity(intentCalificacionActivity);
    }

    private void recibirDatos(){
        Bundle idRecibido = getIntent().getExtras();
        idAsignatura = idRecibido.getString("Id_asignatura");
        idPresentacion = idRecibido.getString("Id_presentacion");
        nombrePresentacion = idRecibido.getString("Nombre_presentacion");
        Log.d("!!!", "Id asignatura = "+idAsignatura+"   Id presentacion = "+idPresentacion);
    }

    private void acabarPresentacion(){
        // TODO modificar field isFinished en base de datos
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("presentaciones").document(idPresentacion).update("isFinished",true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("¡¡¡", "Field isFinished updated TRUE");
                        // TODO Lanzar nueva AcabarActivity nombrePresentacion y media.

                        // media temporal a mano
                        media =8;
                        AcabarActivity.startActivity(CalificacionActivity.this,nombrePresentacion,media);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("¡¡¡", "Error writing document", e);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion);

        recibirDatos();

        // TODO mandar trama BLE con idAsignatura e idPresentacion
        // TODO Display calificaciones en tiempo real

        Button acabarPresentacion = findViewById(R.id.buttonAcabarPresentacion);
        acabarPresentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                acabarPresentacion();
            }
        });

    }
}
