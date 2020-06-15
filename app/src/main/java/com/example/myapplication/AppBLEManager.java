package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;


import java.util.UUID;

/**
 * Esta clase implementa todas las funcionalidades bluetooth que se ncesitan en toda la
 * aplicación
 */
public class AppBLEManager {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private AdvertisingSet currentAdvertisingSet;

    public AppBLEManager(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public boolean isActivated() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void startAdvertising(String advertisingData) {
        bluetoothAdapter.setName(advertisingData);
        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        AdvertisingSetParameters parameters = (new AdvertisingSetParameters.Builder()
                .setLegacyMode(true) // True by default, but set here as a reminder.
                .setConnectable(false).setScannable(true)
                .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
                .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MEDIUM)
                .build());
        advertiser.startAdvertisingSet(parameters, null, null, null, null, callback);

    }

    public void stopAdvertising(){
        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        advertiser.stopAdvertisingSet(callback);
    }

    private ScanListener scanListener;

    public void startScan(ScanListener listener) {
        this.scanListener = listener;
        bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
    }

    public void stopScan() {
        this.scanListener = null;
        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            ScanRecord scanRecord = result.getScanRecord();
            if (scanRecord != null) {
                String deviceName = scanRecord.getDeviceName();
                if(deviceName != null) {
                    Log.d("!!!","Nombre: " +deviceName);
                    if (scanListener != null) {
                        scanListener.onDeviceFound(deviceName);
                    }
                }
            }
        }
    };

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

    public interface ScanListener {
        void onDeviceFound(String deviceName);
    }

}
