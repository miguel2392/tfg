package com.example.myapplication;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppDatabaseManager {

    private static final String nombreField = "nombre";
    private static final String profesoresCollection = "profesores";
    private static final String alumnosCollection = "alumnos";

    public boolean isUserLogged() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    public String getUserId() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            return null;
        }
    }

    public void getProfessor(String uuid, final ProfessorListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(profesoresCollection).document(uuid).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String name = documentSnapshot.getString(nombreField);
                            listener.onProfessorReceived(name);
                        } else {
                            listener.onProfessorReceived(null);
                        }
                    }
                });
    }

    public void getAlumno(String uuid, final AlumnoListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(alumnosCollection).document(uuid).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String name = documentSnapshot.getString(nombreField);
                            listener.onAlumnoReceived(name);
                        } else {
                            listener.onAlumnoReceived(null);
                        }
                    }
                });
    }

    interface ProfessorListener {
        void onProfessorReceived(String name);
    }

    interface AlumnoListener {
        void onAlumnoReceived(String name);
    }
}
