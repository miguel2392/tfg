package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
    private String idPresentacion;
    private EditText et1;
    private String nombrePresentacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        recibirDatos();

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idProfesor = user.getUid();

        Map<String, Object> presentation = new HashMap<>();
        presentation.put("nombre asignatura", name);
        presentation.put("id_asignatura",idAsignatura);
        presentation.put("nombre presentación",nombrePresentacion);
        presentation.put("owner",idProfesor);
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
                });

    }
}
