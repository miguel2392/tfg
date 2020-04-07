package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.nio.charset.Charset;
import java.util.UUID;

public class ActivityB extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private AdvertisingSet currentAdvertisingSet;
    static final int REQUEST_ENABLE_BT = 0;
    private EditText nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_b);

        Button boton=findViewById(R.id.button4);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdvertising();
            }
        });

        Button boton2=findViewById(R.id.button5);
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAdvertising();
            }
        });

        nombre = findViewById(R.id.editText1);

        // Inicializacion Bluetooth Adapter

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Asegura que bluetooth esta activado, si no lo está, pide que se active

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }
    }

    private void startAdvertising(){

        bluetoothAdapter.setName(nombre.getText().toString());

        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        AdvertisingSetParameters parameters = (new AdvertisingSetParameters.Builder())
                .setLegacyMode(true) // True by default, but set here as a reminder.
                .setConnectable(false).setScannable(true)
                .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
                .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MEDIUM)
                .build();



        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(getString(R.string.ble_uuid)));
        //byte[] messageToSend = "Data".getBytes(Charset.forName("UTF-8"));
        byte[] password = new byte[6];
        password[0] =0;
        password[1] =1;
        password[2] =0;
        password[3] =1;
        password[4] =0;
        password[5] =1;



        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceData(pUuid,password)
                //.addServiceUuid(pUuid)
                //.addManufacturerData(224,password)
                .build();
        //AdvertiseData data = (new AdvertiseData.Builder()).setIncludeDeviceName(true).build();


        advertiser.startAdvertisingSet(parameters, data, null, null, null, callback);



        // When done with the advertising:
        //advertiser.stopAdvertisingSet(callback);
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



