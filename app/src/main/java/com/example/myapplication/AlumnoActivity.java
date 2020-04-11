package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    private String idPresentacion;

    public static void startActivity(Context context, String name, String presentacionID) {

        Intent intentAlumnoActivity = new Intent(context, AlumnoActivity.class);
        intentAlumnoActivity
                .putExtra(EXTRA_NAME, name)
                .putExtra("Id presentacion",presentacionID);

        context.startActivity(intentAlumnoActivity);
    }

    private void recibirDatos(){
        Bundle idRecibido = getIntent().getExtras();
        idPresentacion = idRecibido.getString("Id presentacion");
        nombreAlumno = idRecibido.getString(EXTRA_NAME);
        Log.d("!!!", idPresentacion);
    }

    private EditText et1;
    private String idAlumno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno);

        recibirDatos();

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

    private void showFinishDialog(){
        new AlertDialog.Builder(this)
                .setMessage("Su calificación ha sido enviada con éxito")
                .setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
    private void subirNota(int nota, String name){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        idAlumno = user.getUid();

        Map<String, Object> calificacion = new HashMap<>();
        calificacion.put("nombre_alumno", name);
        calificacion.put("calificacion", nota);


        db.collection("presentaciones").document(idPresentacion)
                .collection("calificaciones").document(idAlumno).set(calificacion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("¡¡¡", "DocumentSnapshot successfully written!");
                        showFinishDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("¡¡¡", "Error writing document", e);
                        Toast.makeText(AlumnoActivity.this,"Error al enviar la calificación :(",Toast.LENGTH_LONG).show();
                    }
                });

    }


    }

