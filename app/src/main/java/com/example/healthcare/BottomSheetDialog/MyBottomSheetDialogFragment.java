package com.example.healthcare.BottomSheetDialog;

import static androidx.core.app.ActivityCompat.recreate;
import static com.example.healthcare.MainActivity.TAG;
import static com.example.healthcare.Permissions.BluetoothUtil.REQUEST_ENABLE_BLUETOOTH;
import static com.example.healthcare.Permissions.LocationUtil.REQUEST_ENABLE_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.BluetoothModule.MyBluetoothGattCallback;
import com.example.healthcare.BluetoothModule.ScanResultAdapter;
import com.example.healthcare.Permissions.BluetoothUtil;
import com.example.healthcare.Permissions.LocationUtil;
import com.example.healthcare.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    //classes
    private Timer scanTimer;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanResultAdapter scanResultAdapter;
    private BluetoothAdapter bluetoothAdapter;
    Animation rotation;
    private ScanSettings scanSettings;
    BluetoothGatt gatt;

    //UI views
    ImageView refreshButton;
    Context context;
    Activity activity;
    RecyclerView bluetoothDeviceRecyclerView;
    LinearLayout linearLayoutAvailableDevices;


    //datatypes
    private static final long SCAN_INTERVAL = 5000;
    private List<ScanResult> scanResults = new ArrayList<>();
    private boolean isScanning = false;
    int indexQuery;


    /*  Constructor
    *     params1 - context  */
    public MyBottomSheetDialogFragment(Context context) {
        this.context = context;
    }



    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_layout, container, false);

        // Initialize Bluetooth
        activity = (Activity) context;

        //ble scanner init
        bluetoothLeScanner = getBluetoothLeScanner();
        getScanResultAdapter();

        //Assign Id here
        idAssigningMethod(view);

        //location and Bluetooth check
        LocationUtil.requestFineLocationConnectPermission(activity);
        LocationUtil.requestLocationEnable(activity);
        BluetoothUtil.requestBluetoothEnable(activity, context);

//        further needed settings for scaning
        scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        //Start and Stop scan
        startAndStopScanMethod();

        //set adapter
        //recycler view
        bluetoothDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        bluetoothDeviceRecyclerView.setAdapter(getScanResultAdapter());

        //cancel button Method
        refreshButtonMethod();

        return view;
    }


    //method to start and stop and check scaning
    private void startAndStopScanMethod() {
        if (isScanning) {
            stopPeriodicScan();
        } else {
            startPeriodicScan();
        }
    }

    //start the scan
    private void startPeriodicScan() {
        scanTimer = new Timer();
        scanTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isScanning) {
                        stopBleScan();
                    } else {
                        startBleScan();
                    }
                });
            }
        }, 0, SCAN_INTERVAL);
    }

    //stop the scan
    private void stopPeriodicScan() {
        if (scanTimer != null) {
            scanTimer.cancel();
            scanTimer = null;
        }
        new Handler(Looper.getMainLooper()).post(this::stopBleScan);
    }

    //check the required permission here
    public boolean hasRequiredRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d(TAG, "hasRequiredRuntimePermissions:122 " + hasPermission(Manifest.permission.BLUETOOTH_SCAN));
            Log.d(TAG, "hasRequiredRuntimePermissions:123 " + hasPermission(Manifest.permission.BLUETOOTH_CONNECT));
            return hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                    hasPermission(Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            Log.d(TAG, "hasRequiredRuntimePermissions:127 " + hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
            return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    //check the permission is given as parameter
    public boolean hasPermission(String permissionType) {
        return ContextCompat.checkSelfPermission(context, permissionType) ==
                PackageManager.PERMISSION_GRANTED;
    }

    //start BLE scan
    @SuppressLint("NotifyDataSetChanged")
    private void startBleScan() {
        if (!hasRequiredRuntimePermissions()) {
            requestRelevantRuntimePermissions();
        } else {
            Log.d(TAG, "startBleScan");
            scanResults.clear();
            scanResultAdapter.notifyDataSetChanged();

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    BluetoothUtil.requestBluetoothScanPermission(activity);
                }

            }
            bluetoothLeScanner.startScan(null, scanSettings, scanCallback);
            isScanning = true;

            // Load the animation
            rotation = AnimationUtils.loadAnimation(context, R.anim.rotate_animation);
            refreshButton.startAnimation(rotation);
        }
    }


    //request to enable the runtime permissions
    @SuppressLint("NewApi")
    private void requestRelevantRuntimePermissions() {
        if (hasRequiredRuntimePermissions()) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            BluetoothUtil.requestBluetoothEnable(activity, context);
            BluetoothUtil.requestBluetoothConnectPermission(activity);
            BluetoothUtil.requestBluetoothScanPermission(activity);
            Log.d(TAG, "requestRelevantRuntimePermissions: 171");


        } else {
            LocationUtil.requestFineLocationConnectPermission(activity);
            LocationUtil.requestLocationEnable(activity);
        }

    }

    //stop BLE scan
    public void stopBleScan() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            BluetoothUtil.requestBluetoothScanPermission(activity);
        }
        Log.d(TAG, "stopBleScan: ");
        bluetoothLeScanner.stopScan(scanCallback);
        isScanning = false;

        // Clear the animation
        rotation = AnimationUtils.loadAnimation(context, R.anim.rotate_animation);
        refreshButton.clearAnimation();
    }

    //init Bluetooth adapter
    private BluetoothAdapter getBluetoothAdapter() {
        if (bluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();

        }
        return bluetoothAdapter;
    }

    //init BLE scanner
    private BluetoothLeScanner getBluetoothLeScanner() {
        return getBluetoothAdapter().getBluetoothLeScanner();
    }

    //on click listener for the bluetooth device in the recycler view and connect the device which we click
    @SuppressLint("MissingPermission")
    private ScanResultAdapter getScanResultAdapter() {
        if (scanResultAdapter == null) {
            scanResultAdapter = new ScanResultAdapter(scanResults, item -> {

                    BluetoothDevice device = item.getDevice();
                    Log.w("ScanResultAdapter", "Connecting to " + device.getAddress());
                    gatt = device.connectGatt(context, false, new MyBluetoothGattCallback(context));
                    stopPeriodicScan();

            });
        }
        return scanResultAdapter;
    }

    //refresh button to start the scan
    public void refreshButtonMethod() {
        refreshButton.setOnClickListener(view -> startPeriodicScan());
    }


    //Assign ID for the UI here
    private void idAssigningMethod(View view) {
        refreshButton = view.findViewById(R.id.refreshButton);
        bluetoothDeviceRecyclerView = view.findViewById(R.id.bluetoothDeviceRecyclerView);
        linearLayoutAvailableDevices = view.findViewById(R.id.linearLayoutAvailableDevices);
    }


    //ScanCallback for get the scan result and other funtionalities with the device / handle scan failure
    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Activity activity = (Activity) context;
            indexQuery = -1;
            for (int i = 0; i < scanResults.size(); i++) {
                if (scanResults.get(i).getDevice().getAddress().equals(result.getDevice().getAddress())) {
                    indexQuery = i;
                    break;
                }
            }

            if (indexQuery != -1) {
                // A scan result already exists with the same address
                scanResults.set(indexQuery, result);
                activity.runOnUiThread(() -> scanResultAdapter.notifyItemChanged(indexQuery));
            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        BluetoothUtil.isBluetoothConnectPermissionGranted(activity);
                    }
                }
                String deviceName = result.getDevice().getName() != null ? result.getDevice().getName() : "Unnamed";
//                Log.i(TAG, "Found BLE device! Name: " + deviceName + ", address: " + result.getDevice().getAddress());
                if (result.getDevice().getName() != null) {
                    Log.i(TAG, "Found BLE device! Name: " + result.getDevice().getName());
                    scanResults.add(result);
                }
                activity.runOnUiThread(() -> {
                    if (scanResults.isEmpty()) {
                        scanResultAdapter.notifyItemInserted(scanResults.size() - 1);
                    }
                });
            }

        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("ScanCallback", "onScanFailed: code " + errorCode);

        }
    };

    //for handling the permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean containsPermanentDenial = false;
                boolean containsDenial = false;
                boolean allGranted = true;

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                        containsPermanentDenial = true;
                    }
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        containsDenial = true;
                    }
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                    }
                }

                if (containsPermanentDenial) {
                    Toast.makeText(context, "containsPermanentDenial", Toast.LENGTH_SHORT).show();
                } else if (containsDenial) {
                    requestRelevantRuntimePermissions();
                } else if (allGranted && hasRequiredRuntimePermissions()) {
                    startBleScan();
                } else {
                    // Unexpected scenario encountered when handling permissions
                    recreate(activity);
                }
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == REQUEST_ENABLE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean containsPermanentDenial = false;
                boolean containsDenial = false;
                boolean allGranted = true;

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                        containsPermanentDenial = true;
                    }
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        containsDenial = true;
                    }
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                    }
                }

                if (containsPermanentDenial) {
                    Toast.makeText(context, "containsPermanentDenial", Toast.LENGTH_SHORT).show();
                } else if (containsDenial) {
                    requestRelevantRuntimePermissions();
                } else if (allGranted && hasRequiredRuntimePermissions()) {
                    startBleScan();
                } else {
                    // Unexpected scenario encountered when handling permissions
                    recreate(activity);
                }
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        stopPeriodicScan();

    }

    public void dismissDialog() {
        if (getDialog() != null) {
            getDialog().dismiss();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (gatt != null) {
            gatt.disconnect();
            gatt.close();
            gatt = null;
        }
    }

}