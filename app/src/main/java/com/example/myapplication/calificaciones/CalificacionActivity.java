package com.example.myapplication.calificaciones;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.AcabarActivity;
import com.example.myapplication.AdvertisingDataHelper;
import com.example.myapplication.AppBLEManager;
import com.example.myapplication.AppDatabaseManager;

import com.example.myapplication.R;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.List;

/**
 * En esta actividad el dispositivo comienza a realizar advertising BLE publicando el secreto para poder
 * calificar la presentacion que acaba de crear.
 */
public class CalificacionActivity extends AppCompatActivity {

    private String idAsignatura;
    private String idPresentacion;
    private String nombrePresentacion;
    private ListenerRegistration listener;
    private CalificacionesListView calificacionesListView;
    private Handler handler;
    private AppBLEManager appBLEManager;
    static final int REQUEST_ENABLE_BT = 0;

    private AppDatabaseManager appDatabaseManager;


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
        appBLEManager = new AppBLEManager(this);
        appDatabaseManager = new AppDatabaseManager();
        setContentView(R.layout.activity_calificacion);

        handler = new Handler(Looper.getMainLooper());
        calificacionesListView = findViewById(R.id.calificaciones_list_view);
        recibirDatos();
        setTitle(nombrePresentacion);

        if (appBLEManager.isActivated()) {
            startAdvertising();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (appBLEManager.isActivated()) {
            startAdvertising();
        } else {
            Toast.makeText(CalificacionActivity.this, "Bluetooth not activated", Toast.LENGTH_LONG).show();
        }
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
        appBLEManager.stopAdvertising();
        appDatabaseManager.stopPresentacion(idPresentacion, new AppDatabaseManager.StopPresentacionListener() {
            @Override
            public void onPresentacionFinished(boolean success) {
                if (success) {
                    AcabarActivity.startActivity(CalificacionActivity.this,
                            nombrePresentacion,
                            calificacionesListView.getMedia());
                    finish();
                } else {
                    Toast.makeText(CalificacionActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void empezarEscuchar(){
        appDatabaseManager.listenCalificaciones(idPresentacion, new AppDatabaseManager.CalificacionesListener() {
            @Override
            public void onCalificacionesupdate(List<Calificacion> listaCalificaciones) {
                displayData(listaCalificaciones);
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
        appBLEManager.startAdvertising(idConcatenado);
    }

}
