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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    // Initialize Firebase Auth


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            openAuthenticationActivity(null);
            finish();
        }

        startActivity(new Intent(this,DiferenciacionActivity.class));

    }

    public void openActivityA (View view){
        Intent intentA = new Intent(this, ActivityA.class);
        startActivity(intentA);
    }

    public void openActivityB (View view){
        Intent intentB = new Intent(this, ActivityB.class);
        startActivity(intentB);

    }

    public void signOut (View view){
        FirebaseAuth.getInstance().signOut();
        openAuthenticationActivity(null);
        finish();
    }

    public void openAuthenticationActivity (View view){
        Intent intentSignIn = new Intent(this, AuthenticationActivity.class);
        startActivity(intentSignIn);

    }

    public void leerAsignatura (View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("asignaturas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("!!!", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("!!!", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
