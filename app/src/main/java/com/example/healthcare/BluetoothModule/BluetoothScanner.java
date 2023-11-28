package com.example.healthcare.BluetoothModule;

import static com.example.healthcare.BleDevices.BloodGlucometer.*;
import static com.example.healthcare.BleDevices.ECGMeter.ECG_DEVICE_NAME;
import static com.example.healthcare.BleDevices.WeightScale.WEIGHT_SCALE_DEVICE_NAME;
import static com.example.healthcare.BleDevices.WeightScale.weightScaleReadingsMethod;
import static com.example.healthcare.Fragments.HomeFragment.connectedGatts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BluetoothScanner {

    private static final String TAG = "TAGi";
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter bluetoothAdapter;
    BluetoothGatt gatt;
    public static BluetoothDevice urionBpDevice, bloodGlucometer, ecgMeter;
    public static boolean deviceConnected = false;

    String DEVICE_NAME_SCAN;
    private List<ScanResult> scanResults = new ArrayList<>();
    Context context;
    int indexQuery;
    private Handler handler = new Handler(Looper.getMainLooper());


    /*    Constructor
            params1 - devicename(String)
            params2 - context
    */
    public BluetoothScanner(String deviceName, Context context) {
        this.DEVICE_NAME_SCAN = deviceName;
        this.context = context;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }


    //Start scan
    @SuppressLint("MissingPermission")
    public void startScan() {

        if (bluetoothLeScanner == null) {
            Log.e(TAG, "Bluetooth LE not supported on this device");
            return;
        }

        bluetoothLeScanner.startScan(scanCallback);
        // Stop scan after a certain period of time (30 seconds)
        handler.postDelayed(this::stopScan, 30000);

    }


    //strop scan
    @SuppressLint("MissingPermission")
    public void stopScan() {
        Activity activity = (Activity) context;
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);

//            if ((activity.getCurrentFocus()) != null) {
//                Snackbar.make((activity.getCurrentFocus()), "Device Not Found", Snackbar.LENGTH_SHORT)
//                        .setAction("Retry", view -> startScan())
//                        .show();
//
//            }
        }
    }


    //scan result / handle scan failure -other functionalities
    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Activity activity = (Activity) context;

            //basic check for BLE connect permission
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    BluetoothUtil.isBluetoothConnectPermissionGranted(activity);
                }
            }

            String deviceName = result.getDevice().getName() != null ? result.getDevice().getName() : "Unnamed";
//            Log.i(TAG, "Found BLE device! Name: " + deviceName + ", address: " + result.getDevice().getAddress());


            if (DEVICE_NAME_SCAN.equals(WEIGHT_SCALE_DEVICE_NAME)) {

                //Weight Scale
                weightScaleReadingsMethod(result);

            } else if (Objects.equals(result.getDevice().getName(), DEVICE_NAME_SCAN)) {

                //Urion Blood Pressure
                Log.i(TAG, "Found BLE device! Name: " + result.getDevice().getName());
                urionBpDevice = result.getDevice(); // Store the device for future connection
                if (!deviceConnected) {
                    connectToDevice(activity);
                }

            } else if (BLOOD_GLUCOMETER_DEVICE_NAME.contains(result.getDevice().getName())) {

                //Blood Glucometer
                Log.i(TAG, "Found BLE device! Name: " + result.getDevice().getName());
                bloodGlucometer = result.getDevice(); // Store the device for future connection
                if (!deviceConnected) {
                    connectToDevice(activity);
                }

            } else if (ECG_DEVICE_NAME.contains(result.getDevice().getName())) {

                //ECG meter
                Log.i(TAG, "Found BLE device! Name: " + result.getDevice().getName());
                ecgMeter = result.getDevice(); // Store the device for future connection
                if (!deviceConnected) {
                    connectToDevice(activity);
                }
            }
//            else if (Objects.equals(result.getDevice().getName(), BLOOD_GLUCOMETER_DEVICE_NAME2)) {
//                Log.i(TAG, "Found BLE device! Name: " + result.getDevice().getName());
//                bloodGlucometer = result.getDevice(); // Store the device for future connection
//                if (!deviceConnected) {
//                    connectToDevice(activity);
//                }
//            }


        }


        @Override
        public void onScanFailed(int errorCode) {
            Log.e("ScanCallback", "onScanFailed: code " + errorCode);
        }
    };

    //connected to the Device
    @SuppressLint("MissingPermission")
    private void connectToDevice(Activity activity) {
        if (urionBpDevice != null) {

            //urion Bp
            gatt = urionBpDevice.connectGatt(context, false, new MyBluetoothGattCallback(context));
            if (gatt != null) {
                stopScan();
                deviceConnected = true;
                Log.i(TAG, "Connecting to device: " + urionBpDevice.getName());
                connectedGatts.add(gatt); // for disconnect the device, you need to add the device in tha list
            } else {
                Log.e(TAG, "Failed to connect to device: " + urionBpDevice.getName());
            }

        } else if (bloodGlucometer != null) {

            //Blood Glucometer
            gatt = bloodGlucometer.connectGatt(context, false, new MyBluetoothGattCallback(context));
            if (gatt != null) {
                stopScan();
                deviceConnected = true;
                Log.i(TAG, "Connecting to device: " + bloodGlucometer.getName());
                connectedGatts.add(gatt);  // for disconnect the device, you need to add the device in tha list
            } else {
                Log.e(TAG, "Failed to connect to device: " + bloodGlucometer.getName());
            }

        } else if (ecgMeter != null) {

            //ECG meter
            gatt = ecgMeter.connectGatt(context, false, new MyBluetoothGattCallback(context));
            if (gatt != null) {
                stopScan();
                deviceConnected = true;
                Log.i(TAG, "Connecting to device: " + ecgMeter.getName());
                connectedGatts.add(gatt);  // for disconnect the device, you need to add the device in tha list
            } else {
                Log.e(TAG, "Failed to connect to device: " + ecgMeter.getName());
            }
        }


    }


    //Disconnect the device
    @SuppressLint("MissingPermission")
    public static void disconnectAllDevices() {
        Log.e(TAG, "disconnect All Devices Triggered.. dvb" );
        for (BluetoothGatt gatt : connectedGatts) {
            if (gatt != null) {
                gatt.disconnect();
                gatt.close();
                deviceConnected = false;
                Log.e(TAG, "disconnect All Devices Triggered.. " );
            }
        }
        connectedGatts.clear(); // Clear the list after disconnection
    }

}
