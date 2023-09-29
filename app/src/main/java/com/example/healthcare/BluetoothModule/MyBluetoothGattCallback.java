package com.example.healthcare.BluetoothModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class MyBluetoothGattCallback extends BluetoothGattCallback {
    private static final String TAG = "TAGi";
    @SuppressLint("MissingPermission")
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        String  deviceAddress = gatt.getDevice().getAddress();
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // Device connected, start discovering services
            Log.d(TAG, "onConnectionStateChange: STATE_CONNECTED "+deviceAddress);
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // Device disconnected, handle accordingly
            Log.d(TAG, "onConnectionStateChange: STATE_DISCONNECTED "+deviceAddress);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            // Services discovered, you can now communicate with characteristics
            Log.d(TAG, "onConnectionStateChange: GATT_SUCCESS "+status);
        }
    }

    // Override other relevant methods as needed
}
