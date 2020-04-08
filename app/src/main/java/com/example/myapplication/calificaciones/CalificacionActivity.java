package com.example.myapplication.calificaciones;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.AcabarActivity;
import com.example.myapplication.AdvertisingDataHelper;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class CalificacionActivity extends AppCompatActivity {

    private String idAsignatura;
    private String idPresentacion;
    private String nombrePresentacion;
    private ListenerRegistration listener;
    private CalificacionesListView calificacionesListView;
    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    static final int REQUEST_ENABLE_BT = 0;
    private AdvertisingSet currentAdvertisingSet;


    public static void startActivity(Context context, String asignaturaID, String presentacionID, String nombrePresentacion) {

        Intent intentCalificacionActivity = new Intent(context, CalificacionActivity.class);
        intentCalificacionActivity
                .putExtra("Id_asignatura", asignaturaID)
                .putExtra("Id_presentacion", presentacionID)
                .putExtra("Nombre_presentacion", nombrePresentacion);


        context.startActivity(intentCalificacionActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion);
        handler = new Handler(Looper.getMainLooper());
        calificacionesListView = findViewById(R.id.calificaciones_list_view);
        recibirDatos();
        setTitle(nombrePresentacion);

        // TODO mandar trama BLE con idAsignatura e idPresentacion

        // Inicializacion Bluetooth Adapter
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Asegura que bluetooth esta activado, si no lo está, pide que se active

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        startAdvertising();



        // Boton acabar presentación
        Button acabarPresentacion = findViewById(R.id.buttonAcabarPresentacion);
        acabarPresentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFinishDialog();
            }
        });
        //Automatically start listening to model updates.
        empezarEscuchar();
    }

    @Override
    public void onBackPressed() {
        //Block back navigation
    }

    private void recibirDatos(){
        Bundle idRecibido = getIntent().getExtras();
        idAsignatura = idRecibido.getString("Id_asignatura");
        idPresentacion = idRecibido.getString("Id_presentacion");
        nombrePresentacion = idRecibido.getString("Nombre_presentacion");
        Log.d("!!!", "Id asignatura = "+idAsignatura+"   Id presentacion = "+idPresentacion);
    }

    private void acabarPresentacion(){
        //  modificar field isFinished en base de datos
        listener.remove();
        stopAdvertising();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("presentaciones").document(idPresentacion).update("isFinished",true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("¡¡¡", "Field isFinished updated TRUE");
                        // TODO Lanzar nueva AcabarActivity nombrePresentacion y media.

                        AcabarActivity.startActivity(CalificacionActivity.this,
                                                     nombrePresentacion,
                                                     calificacionesListView.getMedia());
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("¡¡¡", "Error writing document", e);
                    }
                });
    }

    private void empezarEscuchar(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("presentaciones").document(idPresentacion).collection("calificaciones");

                listener = query.addSnapshotListener(new EventListener<QuerySnapshot>(){
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("!!!", "Listen failed.", e);
                            return;
                        }

                        List<Calificacion> calificaciones = new ArrayList<>();
                        Log.d("!!!", "Aqui si llegamos no");
                        for (QueryDocumentSnapshot doc : value) {
                            Log.d("!!!", "Por lo menos aqui llegamos");
                            Log.d("!!!", doc.toString());
                            if (doc.get("calificacion") != null) {
                                Calificacion nota = new Calificacion(doc.getString("nombre_alumno"),doc.getLong("calificacion"));
                                calificaciones.add(nota);
                                Log.d("!!!", "Notas " + calificaciones);

                            }
                        }
                        displayData(calificaciones);
                        //Log.d("!!!", "Notas " + calificaciones);
                    }
                });
    }

    private void displayData(final List<Calificacion> listaCalificaciones){
        handler.post(new Runnable() {
            @Override
            public void run() {
                calificacionesListView.setCalificaciones(listaCalificaciones);
            }
        });
    }

    private void leerDatos(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("presentaciones").document(idPresentacion).collection("calificaciones").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d("!!!", "GET COMPLETADO");
                        }
                        else {
                            Log.w("!!!", "Error getting documents.", task.getException());
                        }
                    }

                    });
    }

    private void showFinishDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Terminar presentacion")
                .setMessage("¿Esta seguro de que quiere terminar la presentación?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        acabarPresentacion();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void startAdvertising(){


        String idConcatenado = AdvertisingDataHelper.generateDeviceName(idAsignatura,idPresentacion);
        bluetoothAdapter.setName(idConcatenado);

        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        AdvertisingSetParameters parameters = (new AdvertisingSetParameters.Builder())
                .setLegacyMode(true) // True by default, but set here as a reminder.
                .setConnectable(false).setScannable(true)
                .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
                .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MEDIUM)
                .build();

        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(getString(R.string.ble_uuid)));

        //Pasar los dos String a mandar a un array de bytes

        byte[] serviceData = new byte[28];
        int bufferPosition = 0;

        //String idAsignatura

        /*byte[] bytesIdAsignatura = idAsignatura.getBytes();
        System.arraycopy(bytesIdAsignatura,0,serviceData,bufferPosition,bytesIdAsignatura.length);
        bufferPosition += bytesIdAsignatura.length;

        //String idPresentacion

        byte [] bytesIdPresentacion = idPresentacion.getBytes();
        System.arraycopy(bytesIdPresentacion,0,serviceData,bufferPosition,bytesIdPresentacion.length);

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceData(pUuid,serviceData)
                .build();*/

        // Concatenando Strings


        byte [] serviceData2 = idConcatenado.getBytes();
        AdvertiseData data2 = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addServiceData(pUuid,serviceData2)
                .build();
        advertiser.startAdvertisingSet(parameters, data2, null, null, null, callback);

        //advertiser.startAdvertisingSet(parameters, data, null, null, null, callback);

    }

    private void onAdvertisingStart(){
        // After onAdvertisingSetStarted callback is called, you can modify the
        // advertising data and scan response data:
        currentAdvertisingSet.setAdvertisingData(new AdvertiseData.Builder().
                setIncludeDeviceName(true).setIncludeTxPowerLevel(true).build());
        // Wait for onAdvertisingDataSet callback...
        currentAdvertisingSet.setScanResponseData(new
                AdvertiseData.Builder().addServiceUuid(new ParcelUuid(UUID.randomUUID())).build());
        // Wait for onScanResponseDataSet callback...
    }

    private void stopAdvertising(){

        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        advertiser.stopAdvertisingSet(callback);
    }

    private AdvertisingSetCallback callback = new AdvertisingSetCallback() {
        @Override
        public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
            //Log.i(LOG_TAG, "onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
            //      + status);
            currentAdvertisingSet = advertisingSet;
            onAdvertisingStart();
        }

        @Override
        public void onAdvertisingDataSet(AdvertisingSet advertisingSet, int status) {
            //Log.i(LOG_TAG, "onAdvertisingDataSet() :status:" + status);
        }

        @Override
        public void onScanResponseDataSet(AdvertisingSet advertisingSet, int status) {
            // Log.i(LOG_TAG, "onScanResponseDataSet(): status:" + status);
        }

        @Override
        public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
            // Log.i(LOG_TAG, "onAdvertisingSetStopped():");
        }


    };
}
