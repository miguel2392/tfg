package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DiferenciacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diferenciacion);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user==null){
            startActivity(new Intent(this,AuthenticationActivity.class));
            finish();
        }
        else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("profesores").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                //lanzar activity profesor
                                String name = documentSnapshot.getString("nombre");
                                ProfesorActivity.startActivity(DiferenciacionActivity.this, name);
                            } else{
                                //lanzar activity alumno
                            }
                        }
                    });

        }
    }
}
