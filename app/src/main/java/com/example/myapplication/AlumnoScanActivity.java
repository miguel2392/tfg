package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import com.example.myapplication.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class AlumnoScanActivity extends AppCompatActivity {

    static final int REQUEST_ENABLE_BT = 0;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler handler;
    private HashSet<String> macs = new HashSet<>();
    private HashMap<String, Integer> rssiMap = new HashMap<>();
    private ScanRecord scanRecord;
    private List<String>asignaturas;
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
        setContentView(R.layout.activity_activity_a);
        nombreAlumno = getIntent().getStringExtra(EXTRA_NAME);
        setTitle(nombreAlumno);
        boton=findViewById(R.id.button3);
        boton.setEnabled(false);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanLeDevice(true);
            }
        });



        handler = new Handler(Looper.getMainLooper());

        // Inicializacion Bluetooth Adapter

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Asegura que bluetooth esta activado, si no lo está, pide que se active

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }

        getAsignaturas();

    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            macs.add(result.getDevice().getAddress());
            rssiMap.put(result.getDevice().getAddress(), result.getRssi());
            scanRecord = result.getScanRecord();
            if (scanRecord != null) {
                String deviceName = scanRecord.getDeviceName();
                if(deviceName!=null){
                Log.d("!!!","Nombre: " +deviceName);
                Pair<String,String> resultado = AdvertisingDataHelper.recoverIds(deviceName);
                if (resultado !=null){

                    //  Mirar asignatura id esta en la lista de asignaturas del alumno
                    if (asignaturas.contains(resultado.first)){

                        scanLeDevice(false);
                        // intent a alumnoActivity pasandole idPresentacion y el nombre del alumno
                        AlumnoActivity.startActivity(AlumnoScanActivity.this,nombreAlumno,resultado.second);
                    }
                }
                }
            }



        }
    };




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
            macs.clear();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mScanning){
                    mScanning = false;
                    bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                    showTimeoutDialog();
                    }
                }
                }, SCAN_PERIOD);

            mScanning = true;

            bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);

        } else {
            mScanning = false;
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {


            if (resultCode == RESULT_OK) {


                Log.d("!!!", "OK MAKAY");
            } else {
                Log.d("!!!", "no OK MAKAY");
                finish();

            }


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


    public void printScanRecord (byte[] scanRecord) {

        // Simply print all raw bytes
        try {
            String decodedRecord = new String(scanRecord,"UTF-8");
            Log.d("DEBUG","decoded Stringbytes : " + ByteArrayToString(scanRecord));
            Log.d("DEBUG","decoded String : " + decodedRecord);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Parse data bytes into individual records
        List<AdRecord> records = AdRecord.parseScanRecord(scanRecord);


        // Print individual records
        if (records.size() == 0) {
            Log.i("DEBUG", "Scan Record Empty");
        } else {
            Log.i("DEBUG", "Scan Record: " + TextUtils.join(",", records));
        }

    }


    public static String ByteArrayToString(byte[] ba) {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            hex.append(b + " ");

        return hex.toString();
    }


    public static class AdRecord {

        public AdRecord(int length, int type, byte[] data) {
            String decodedRecord = "";
            try {
                decodedRecord = new String(data,"UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String dataString=new String(data);
            Log.d("DEBUG", "Length: " + length + " Type : " + type + " Data : " + ByteArrayToString(data));
            Log.d("DEBUG", "Length: " + length + " Type : " + type + " DataString : " + dataString);
        }



        public static List<AdRecord> parseScanRecord(byte[] scanRecord) {
            List<AdRecord> records = new ArrayList<AdRecord>();

            int index = 0;
            while (index < scanRecord.length) {
                int length = scanRecord[index++];
                //Done once we run out of records
                if (length == 0) break;

                int type = scanRecord[index];
                //Done if our record isn't a valid type
                if (type == 0) break;

                byte[] data = Arrays.copyOfRange(scanRecord, index+1, index+length);

                records.add(new AdRecord(length, type, data));
                //Advance
                index += length;
            }

            return records;
        }


    }

    private void getAsignaturas() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("asignaturas").whereArrayContainsAny("alumnos", Arrays.asList(user.getUid()))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                     asignaturas = new LinkedList<>();
                    for (QueryDocumentSnapshot document: task.getResult()){
                        Asignatura asignatura = new Asignatura(
                                document.getId(),
                                document.getString("nombre"),
                                (List<String>) document.get("alumnos"),
                                (List<String>) document.get("profesores")
                        );
                        asignaturas.add(asignatura.id);
                        Log.d("!!!", document.getId() + " => " + document.getData());
                    }
                    boton.setEnabled(true);
                } else {
                    Log.w("!!!", "Error getting documents.", task.getException());
                }
            }
        });
    }
}
