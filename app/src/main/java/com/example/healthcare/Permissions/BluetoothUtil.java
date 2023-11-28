package com.example.healthcare.Permissions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class BluetoothUtil {

    private static final String TAG = "TAGi";
    public static final int REQUEST_ENABLE_BLUETOOTH = 124;
    private static final int BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE = 125;
    private static final int BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE = 126;

    private static final int BLUETOOTH_CONNECT_REQUEST_CODE = 1001;


    //Check Bluetooth is not null and Enabled
    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    /*
        request to Enable Bluetooth
            params1 - Activity
            params2 - Context
    */
    public static boolean requestBluetoothEnable(Activity activity, Context context) {
        if (!isBluetoothEnabled()) {
            Log.d(TAG, "requestBluetoothEnable: BluetoothUtil");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //Intent for Request enable
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestBluetoothConnectPermission(activity);

            }
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            return false;
        }
        return true;
    }

    public static void requestBluetoothEnableAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Bluetooth Services Required");
        builder.setMessage("Please enable Bluetooth services to proceed.");
        builder.setPositiveButton("Ok", null);
        builder.show();
    }


    /*
        Method to check BLUETOOTH_CONNECT permission granted or not
            params1 - Activity
    */
    @RequiresApi(api = Build.VERSION_CODES.S)
    public static boolean isBluetoothConnectPermissionGranted(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }


    /*
        Method to check BLUETOOTH_SCAN permission granted or not
            params1 - Activity
    */
    public static boolean isBluetoothScanPermissionGranted(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }


    /*
        Method to request BLUETOOTH_SCAN permission
            params1 - Activity
    */
    public static boolean requestBluetoothConnectPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isBluetoothConnectPermissionGranted(activity)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE);
            return false;
            }
        }
        return true;
    }


    /*
        Method to request BLUETOOTH_SCAN permission
            params1 - Activity
    */
    public static boolean requestBluetoothScanPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isBluetoothScanPermissionGranted(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }


}
