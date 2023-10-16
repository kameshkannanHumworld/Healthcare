package com.example.healthcare.BluetoothModule;

import static com.example.healthcare.BleDevices.BloodGlucometer.*;
import static com.example.healthcare.BleDevices.WeightScale.WEIGHT_SCALE_DEVICE_NAME;
import static com.example.healthcare.BleDevices.WeightScale.weightScaleReadingsMethod;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.healthcare.Permissions.BluetoothUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BluetoothScanner {

    private static final String TAG = "TAGi";
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter bluetoothAdapter;
    public static BluetoothDevice urionBpDevice, bloodGlucometer;
    public static boolean deviceConnected = false;

    String DEVICE_NAME_SCAN;
    private List<ScanResult> scanResults = new ArrayList<>();
    Context context;
    int indexQuery;
    private Handler handler = new Handler(Looper.getMainLooper());

    public BluetoothScanner(String deviceName, Context context) {
        this.DEVICE_NAME_SCAN = deviceName;
        this.context = context;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    @SuppressLint("MissingPermission")
    public void startScan() {

        if (bluetoothLeScanner == null) {
            Log.e(TAG, "Bluetooth LE not supported on this device");
            return;
        }

        bluetoothLeScanner.startScan(scanCallback);
        // Stop scan after a certain period of time (15 seconds)
        handler.postDelayed(this::stopScan, 15000);

    }

    @SuppressLint("MissingPermission")
    public void stopScan() {
        Activity activity = (Activity) context;
        if (bluetoothLeScanner != null) {

            bluetoothLeScanner.stopScan(scanCallback);
            if ((activity.getCurrentFocus()) != null) {
                Snackbar.make((activity.getCurrentFocus()), "Device Not Found", Snackbar.LENGTH_SHORT)
                        .setAction("Retry", view -> startScan())
                        .show();

            }
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Activity activity = (Activity) context;

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    BluetoothUtil.isBluetoothConnectPermissionGranted(activity);
                }
            }
            String deviceName = result.getDevice().getName() != null ? result.getDevice().getName() : "Unnamed";
//            Log.i(TAG, "Found BLE device! Name: " + deviceName + ", address: " + result.getDevice().getAddress());


            //Weight Scale
            if (DEVICE_NAME_SCAN.equals(WEIGHT_SCALE_DEVICE_NAME)) {
                weightScaleReadingsMethod(result, context);

            }


            //Urion Blood Pressure
            if (Objects.equals(result.getDevice().getName(), DEVICE_NAME_SCAN)) {
                Log.i(TAG, "Found BLE device! Name: " + result.getDevice().getName());
                urionBpDevice = result.getDevice(); // Store the device for future connection
                if (!deviceConnected) {
                    connectToDevice(activity);
                }
            }


            //Blood Glucometer
            if (Objects.equals(result.getDevice().getName(), DEVICE_NAME_SCAN)) {
                Log.i(TAG, "Found BLE device! Name: " + result.getDevice().getName());
                bloodGlucometer = result.getDevice(); // Store the device for future connection
                if (!deviceConnected) {
                    connectToDevice(activity);
                }
            } else if (Objects.equals(result.getDevice().getName(), BLOOD_GLUCOMETER_DEVICE_NAME2)) {
                Log.i(TAG, "Found BLE device! Name: " + result.getDevice().getName());
                bloodGlucometer = result.getDevice(); // Store the device for future connection
                if (!deviceConnected) {
                    connectToDevice(activity);
                }
            }


        }

        @SuppressLint("MissingPermission")
        private void connectToDevice(Activity activity) {
            //urion Bp
            if (urionBpDevice != null) {
                if (urionBpDevice.connectGatt(context, false, new MyBluetoothGattCallback(context)) != null) {
                    stopScan();
                    deviceConnected = true;
                    Log.i(TAG, "Connecting to device: " + urionBpDevice.getName());
                } else {
                    Log.e(TAG, "Failed to connect to device: " + urionBpDevice.getName());
                }
            }

            //Blood Glucometer
            else if (bloodGlucometer != null) {
                if (bloodGlucometer.connectGatt(context, false, new MyBluetoothGattCallback(context)) != null) {
                    stopScan();
                    deviceConnected = true;
                    Log.i(TAG, "Connecting to device: " + bloodGlucometer.getName());
                } else {
                    Log.e(TAG, "Failed to connect to device: " + bloodGlucometer.getName());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("ScanCallback", "onScanFailed: code " + errorCode);
        }
    };
}
