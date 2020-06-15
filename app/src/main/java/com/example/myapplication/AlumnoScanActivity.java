package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.util.LinkedList;
import java.util.List;

/**
 * En esta actividad el alumno puede escanear dispositivos BLE en busca de presentaciones activas.
 */
public class AlumnoScanActivity extends AppCompatActivity {

    static final int REQUEST_ENABLE_BT = 0;
    private static final long SCAN_PERIOD = 10000;
    private boolean mScanning;
    private Handler handler;
    private AppBLEManager appBLEManager;
    private List<String> asignaturasIds;
    private static String EXTRA_NAME = "EXTRA_NAME";
    private String nombreAlumno;
    private Button boton;

    public static void startActivity(Context context, String name) {

        Intent intent = new Intent(context, AlumnoScanActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBLEManager = new AppBLEManager(this);
        setContentView(R.layout.activity_activity_a);
        nombreAlumno = getIntent().getStringExtra(EXTRA_NAME);
        setTitle(nombreAlumno);
        boton=findViewById(R.id.button3);
        boton.setEnabled(false);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appBLEManager.isActivated()) {
                    scanLeDevice(true);
                }
            }
        });

        handler = new Handler(Looper.getMainLooper());

        if (!appBLEManager.isActivated()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        getAsignaturas();

    }

    private void showTimeoutDialog(){
        new AlertDialog.Builder(this)
                .setMessage("No se ha encontrado ninguna presentación")
                .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mScanning){
                        mScanning = false;
                        appBLEManager.stopScan();
                        showTimeoutDialog();
                    }
                }
                }, SCAN_PERIOD);

            mScanning = true;

            appBLEManager.startScan(new AppBLEManager.ScanListener() {
                @Override
                public void onDeviceFound(String deviceName) {
                    Pair<String,String> resultado = AdvertisingDataHelper.recoverIds(deviceName);
                    if (resultado != null ){

                        //  Mirar asignatura id esta en la lista de asignaturas del alumno
                        if (asignaturasIds.contains(resultado.first)) {
                            scanLeDevice(false);
                            // intent a alumnoActivity pasandole idPresentacion y el nombre del alumno
                            AlumnoActivity.startActivity(AlumnoScanActivity.this,nombreAlumno,resultado.second);
                        }
                    }
                }
            });

        } else {
            mScanning = false;
            appBLEManager.stopScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!appBLEManager.isActivated()) {
            finish();
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
        AppAuthManager appAuthManager = new AppAuthManager();
        appAuthManager.signOut();
        Intent intentSignIn = new Intent(this, AuthenticationActivity.class);
        startActivity(intentSignIn);
        finish();
    }

    private void getAsignaturas() {
        AppDatabaseManager appDatabaseManager = new AppDatabaseManager();
        appDatabaseManager.getAsignaturas(false, new AppDatabaseManager.AsiganturasListener() {
            @Override
            public void onAsignaturasRecevied(List<Asignatura> asignaturas) {
                asignaturasIds = new LinkedList<>();
                for (Asignatura asignatura : asignaturas) {
                    asignaturasIds.add(asignatura.id);
                }
                boton.setEnabled(true);
            }
        });
    }
}
