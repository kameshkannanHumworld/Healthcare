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

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    public static void requestBluetoothEnable(Activity activity, Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "requestBluetoothEnable: BluetoothUtil");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);

        }
    }

    public static void requestBluetoothEnableAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Bluetooth Services Required");
        builder.setMessage("Please enable Bluetooth services to proceed.");
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static boolean isBluetoothConnectPermissionGranted(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isBluetoothScanPermissionGranted(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void requestBluetoothConnectPermission(Activity activity) {
        if (!isBluetoothConnectPermissionGranted(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE);
        }
    }

    public static void requestBluetoothScanPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isBluetoothScanPermissionGranted(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE);
        }
    }


}

