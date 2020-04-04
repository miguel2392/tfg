package com.example.myapplication.calificaciones;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.AcabarActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CalificacionActivity extends AppCompatActivity {

    private String idAsignatura;
    private String idPresentacion;
    private String nombrePresentacion;
    private ListenerRegistration listener;
    private CalificacionesListView calificacionesListView;
    private Handler handler;


    public static void startActivity(Context context, String asignaturaID, String presentacionID, String nombrePresentacion) {

        Intent intentCalificacionActivity = new Intent(context, CalificacionActivity.class);
        intentCalificacionActivity
                .putExtra("Id_asignatura", asignaturaID)
                .putExtra("Id_presentacion", presentacionID)
                .putExtra("Nombre_presentacion", nombrePresentacion);


        context.startActivity(intentCalificacionActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion);
        handler = new Handler(Looper.getMainLooper());
        calificacionesListView = findViewById(R.id.calificaciones_list_view);
        recibirDatos();
        setTitle(nombrePresentacion);

        // TODO mandar trama BLE con idAsignatura e idPresentacion

        // Boton acabar presentación
        Button acabarPresentacion = findViewById(R.id.buttonAcabarPresentacion);
        acabarPresentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFinishDialog();
            }
        });
        //Automatically start listening to model updates.
        empezarEscuchar();
    }

    @Override
    public void onBackPressed() {
        //Block back navigation
    }

    private void recibirDatos(){
        Bundle idRecibido = getIntent().getExtras();
        idAsignatura = idRecibido.getString("Id_asignatura");
        idPresentacion = idRecibido.getString("Id_presentacion");
        nombrePresentacion = idRecibido.getString("Nombre_presentacion");
        Log.d("!!!", "Id asignatura = "+idAsignatura+"   Id presentacion = "+idPresentacion);
    }

    private void acabarPresentacion(){
        //  modificar field isFinished en base de datos
        listener.remove();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("presentaciones").document(idPresentacion).update("isFinished",true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("¡¡¡", "Field isFinished updated TRUE");
                        // TODO Lanzar nueva AcabarActivity nombrePresentacion y media.

                        AcabarActivity.startActivity(CalificacionActivity.this,
                                                     nombrePresentacion,
                                                     calificacionesListView.getMedia());
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

    private void empezarEscuchar(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("presentaciones").document(idPresentacion).collection("calificaciones");

                listener = query.addSnapshotListener(new EventListener<QuerySnapshot>(){
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("!!!", "Listen failed.", e);
                            return;
                        }

                        List<Calificacion> calificaciones = new ArrayList<>();
                        Log.d("!!!", "Aqui si llegamos no");
                        for (QueryDocumentSnapshot doc : value) {
                            Log.d("!!!", "Por lo menos aqui llegamos");
                            Log.d("!!!", doc.toString());
                            if (doc.get("calificacion") != null) {
                                Calificacion nota = new Calificacion(doc.getString("nombre_alumno"),doc.getLong("calificacion"));
                                calificaciones.add(nota);
                                Log.d("!!!", "Notas " + calificaciones);

                            }
                        }
                        displayData(calificaciones);
                        //Log.d("!!!", "Notas " + calificaciones);
                    }
                });
    }

    private void displayData(final List<Calificacion> listaCalificaciones){
        handler.post(new Runnable() {
            @Override
            public void run() {
                calificacionesListView.setCalificaciones(listaCalificaciones);
            }
        });
    }

    private void leerDatos(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("presentaciones").document(idPresentacion).collection("calificaciones").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d("!!!", "GET COMPLETADO");
                        }
                        else {
                            Log.w("!!!", "Error getting documents.", task.getException());
                        }
                    }

                    });
    }

    private void showFinishDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Terminar presentacion")
                .setMessage("¿Esta seguro de que quiere terminar la presentación?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        acabarPresentacion();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
