package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PresentacionActivity extends AppCompatActivity {

    private String idAsignatura;
    private String name;
    private String idPresentacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        recibirDatos();



        Button empezarPresentacion = findViewById(R.id.buttonEmpezarPresentacion);
        empezarPresentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // método para crear documentos en la base de datos
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> presentation = new HashMap<>();
        presentation.put("nombre", name);
        presentation.put("id_asignatura",idAsignatura);
        AutoId generador = new AutoId();

        idPresentacion = generador.autoId(20);
        db.collection("presentaciones").document(idPresentacion).set(presentation)
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
                });;

    }
}
