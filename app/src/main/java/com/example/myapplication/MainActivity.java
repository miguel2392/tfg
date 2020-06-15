package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    // Initialize Firebase Auth


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent intentSignIn = new Intent(this, AuthenticationActivity.class);
            startActivity(intentSignIn);
            finish();
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("profesores").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                //lanzar activity profesor
                                String name = documentSnapshot.getString("nombre");
                                ProfesorActivity.startActivity(MainActivity.this, name);
                                finish();
                            } else{

                                FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                                db2.collection("alumnos").document(user.getUid()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                if (documentSnapshot != null && documentSnapshot.exists()) {
                                                    //lanzar activity alumno
                                                    String name2 = documentSnapshot.getString("nombre");
                                                    AlumnoScanActivity.startActivity(MainActivity.this, name2);
                                                    finish();
                                                } else{
                                                    throw new IllegalStateException();
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }



    }





    public void signOut (View view){
        FirebaseAuth.getInstance().signOut();
        Intent intentSignIn = new Intent(this, AuthenticationActivity.class);
        startActivity(intentSignIn);
        finish();
    }




}
