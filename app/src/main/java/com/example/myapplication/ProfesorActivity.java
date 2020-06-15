package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        setTitle(name);
        nameText.setText("Asignaturas");
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

                    PresentacionActivity.startActivity(ProfesorActivity.this,asignatura.id,asignatura.name);
                }
            });
            mainContainer.addView(itemView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionlogout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_logout){
            showLogOutDialog();
        }
        return true;
    }

    private void showLogOutDialog(){
        new AlertDialog.Builder(this)
                .setMessage("¿Quieres cerrar sesión?")
                .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                })
                .setNegativeButton("Volver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void signOut (){
        FirebaseAuth.getInstance().signOut();
        Intent intentSignIn = new Intent(this, AuthenticationActivity.class);
        startActivity(intentSignIn);
        finish();
    }
}
