package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActivityA extends AppCompatActivity {

    static final int REQUEST_ENABLE_BT = 0;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler handler;
    private HashSet<String> macs = new HashSet<>();
    private HashMap<String, Integer> rssiMap = new HashMap<>();
    private ScanRecord scanRecord;
    private List<String>asignaturas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_a);
        Button boton=findViewById(R.id.button3);
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

    private ScanCallback scanCallbackBueno = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            macs.add(result.getDevice().getAddress());
            rssiMap.put(result.getDevice().getAddress(), result.getRssi());
            scanRecord = result.getScanRecord();
            if (scanRecord != null) {
                String deviceName = scanRecord.getDeviceName();
                Log.d("!!!","Nombre: " +deviceName);
                Pair<String,String> resultado = AdvertisingDataHelper.recoverIds(deviceName);
                if (resultado !=null){
                    // TODO Mirar asignatura id esta en la lista de asignaturas del alumno
                    if (asignaturas.contains(resultado.first)){
                        scanLeDevice(false);
                        // TODO intent a alumnoActivity pasandole idPresentacion
                    }
                }
            }

            Log.d("!!!","result: " + result.toString());

        }
    };






    private void scanLeDevice(final boolean enable) {
        if (enable) {
            macs.clear();
                // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mScanning){
                    mScanning = false;
                    //bluetoothAdapter.stopLeScan(scanCallback);
                    bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallbackBueno);
                    }
                }
                }, SCAN_PERIOD);

            mScanning = true;

            bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallbackBueno);

        } else {
            mScanning = false;
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallbackBueno);

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
        // TODO buscar la lista de asignaturas del alumno en database
        // TODO dar valor a la lista de asignaturas
    }
}
