package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ProfesorActivity extends AppCompatActivity {
    private static String EXTRA_NAME = "EXTRA_NAME";
    public static void startActivity(Context context, String name) {

        Intent intent = new Intent(context, ProfesorActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        context.startActivity(intent);
    }

    private LinearLayout mainContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor);

        String name = getIntent().getStringExtra(EXTRA_NAME);
        mainContainer = findViewById(R.id.asignaturas_container);
        TextView nameText = findViewById(R.id.teacher_name);
        nameText.setText(name);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user==null){
            startActivity(new Intent(this,AuthenticationActivity.class));
            finish();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("asignaturas").whereArrayContainsAny("profesores",Arrays.asList(user.getUid()))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Asignatura> asignaturas = new LinkedList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Asignatura asignatura = new Asignatura(
                                document.getId(),
                                document.getString("nombre"),
                                (List<String>) document.get("alumnos"),
                                (List<String>) document.get("profesores")
                                );
                        asignaturas.add(asignatura);
                        Log.d("!!!", document.getId() + " => " + document.getData());
                    }
                    displayAsignaturas(asignaturas);
                } else {
                    Log.w("!!!", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void displayAsignaturas(List<Asignatura> asignaturas) {
        mainContainer.removeAllViews();
        for (Asignatura asignatura : asignaturas) {
            AsignaturaItemView itemView = new AsignaturaItemView(this);
            itemView.bindData(asignatura);
            itemView.setOnAsignaturaClickListener(new AsignaturaClickListener() {
                @Override
                public void onAsignaturaClick(Asignatura asignatura) {
                    // TODO escribir intent al nuevo presentacionactivity pasando ID asignatura como parámetro

                }
            });
            mainContainer.addView(itemView);
        }
    }

}
