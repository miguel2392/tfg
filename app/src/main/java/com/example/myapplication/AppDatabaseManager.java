package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.calificaciones.Calificacion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Esta clase implementa todas las llamadas a la base de datos firebase que se necesitan en
 * toda la aplicación.
 */
public class AppDatabaseManager {

    private static final String nombreField = "nombre";
    private static final String profesoresCollection = "profesores";
    private static final String profesoresField = "profesores";
    private static final String alumnosCollection = "alumnos";
    private static final String alumnosField = "alumnos";
    private static final String asignaturasCollection = "asignaturas";
    private static final String presentacionesCollection = "presentaciones";

    private AppAuthManager appAuthManager;

    public AppDatabaseManager() {
        appAuthManager = new AppAuthManager();
    }

    public void getProfessor(final ProfessorListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(profesoresCollection).document(appAuthManager.getUserId()).get().addOnCompleteListener(
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

    public void getAlumno(final AlumnoListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(alumnosCollection).document(appAuthManager.getUserId()).get().addOnCompleteListener(
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

    public void getAsignaturas(boolean isTeacher, final AsiganturasListener listener) {

        String field = isTeacher ? profesoresField : alumnosField;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(asignaturasCollection).whereArrayContainsAny(field, Arrays.asList(appAuthManager.getUserId()))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Asignatura> asignaturas = new LinkedList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Asignatura asignatura = new Asignatura(
                                document.getId(),
                                document.getString(nombreField),
                                (List<String>) document.get(alumnosField),
                                (List<String>) document.get(profesoresField)
                        );
                        asignaturas.add(asignatura);
                        Log.d("!!!", document.getId() + " => " + document.getData());
                    }
                    listener.onAsignaturasRecevied(asignaturas);
                } else {
                    Log.e("!!!", "Error getting documents.", task.getException());
                    //TODO hanlde error on app
                }
            }
        });
    }

    public void createPresentacion(String asignaturaName,
                                   String asignaturaId,
                                   String presentacionName,
                                   final PresentacionCreationListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String idProfesor = appAuthManager.getUserId();
        Map<String, Object> presentation = new HashMap<>();
        presentation.put("nombre asignatura", asignaturaName);
        presentation.put("id_asignatura", asignaturaId);
        presentation.put("nombre presentación", presentacionName);
        presentation.put("owner",idProfesor);
        presentation.put("isFinished",false);

        AutoId generador = new AutoId();
        final String idPresentacion = generador.autoId(20);

        db.collection(presentacionesCollection).document(idPresentacion).set(presentation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("¡¡¡", "DocumentSnapshot successfully written!");
                listener.onFinish(idPresentacion);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("¡¡¡", "Error writing document", e);
                listener.onFinish(null);
            }
        });
    }

    private ListenerRegistration internalListener;


    public void listenCalificaciones(String idPresentacion,
                                     final CalificacionesListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection(presentacionesCollection).document(idPresentacion).collection("calificaciones");

        internalListener = query.addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("!!!", "Listen failed.", e);
                    return;
                }

                List<Calificacion> calificaciones = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    Log.d("!!!", doc.toString());
                    if (doc.get("calificacion") != null) {
                        Calificacion nota = new Calificacion(doc.getString("nombre_alumno"),doc.getLong("calificacion"));
                        calificaciones.add(nota);
                        Log.d("!!!", "Notas " + calificaciones);

                    }
                }
                listener.onCalificacionesupdate(calificaciones);

            }
        });
    }

    public void stopPresentacion(String idPresentacion,
                                 final StopPresentacionListener stopPresentacionListener) {
        internalListener.remove();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(presentacionesCollection).document(idPresentacion).update("isFinished",true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("¡¡¡", "Field isFinished updated TRUE");
                        stopPresentacionListener.onPresentacionFinished(true);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("¡¡¡", "Error writing document", e);
                        stopPresentacionListener.onPresentacionFinished(false);
                    }
                });
    }

    public void subirNota(String idPresentacion, int nota, String name, final SubirNotaListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> calificacion = new HashMap<>();
        calificacion.put("nombre_alumno", name);
        calificacion.put("calificacion", nota);


        db.collection(presentacionesCollection).document(idPresentacion)
                .collection("calificaciones").document(appAuthManager.getUserId()).set(calificacion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("¡¡¡", "DocumentSnapshot successfully written!");
                        listener.onSubirNotaFinished(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("¡¡¡", "Error writing document", e);
                        listener.onSubirNotaFinished(false);
                    }
                });
    }




    public interface ProfessorListener {
        void onProfessorReceived(String name);
    }

    public interface AlumnoListener {
        void onAlumnoReceived(String name);
    }

    public interface AsiganturasListener {
        void onAsignaturasRecevied(List<Asignatura> asignaturas);
    }

    public interface PresentacionCreationListener {
        void onFinish(String generatedId);
    }

    public interface CalificacionesListener {
        void onCalificacionesupdate(List<Calificacion> listaCalificaciones);
    }

    public interface StopPresentacionListener {
        void onPresentacionFinished(boolean success);
    }

    public interface SubirNotaListener {
        void onSubirNotaFinished(boolean success);
    }
}
