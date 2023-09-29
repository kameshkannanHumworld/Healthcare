package com.example.healthcare.BottomSheetDialog;

import static android.app.Activity.RESULT_OK;

import static androidx.core.app.ActivityCompat.recreate;


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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.BluetoothModule.MyBluetoothGattCallback;
import com.example.healthcare.BluetoothModule.ScanResultAdapter;
import com.example.healthcare.Permissions.BluetoothUtil;
import com.example.healthcare.Permissions.LocationUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.healthcare.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private Timer scanTimer;
    private static final long SCAN_INTERVAL = 5000;
    private static final String TAG = "TAGi";
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanResultAdapter scanResultAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private ScanSettings scanSettings;
    private List<ScanResult> scanResults = new ArrayList<>();
    private boolean isScanning = false;


    ImageView cancelButton;
    int indexQuery;
    Context context;

    RecyclerView bluetoothDeviceRecyclerView;

    LinearLayout linearLayoutAvailableDevices;

    public MyBottomSheetDialogFragment(Context context) {
        this.context = context;
    }


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_layout, container, false);

        // Initialize Bluetooth
        bluetoothLeScanner = getBluetoothLeScanner();
        getScanResultAdapter();

        //Assign Id here
        idAssigningMethod(view);

        //location and Bluetooth check
        LocationUtil.requestFineLocationConnectPermission(requireActivity());
        LocationUtil.requestLocationEnable(requireActivity());
        BluetoothUtil.requestBluetoothEnable(requireActivity(), requireContext());

//        further needed settings
        scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        //Start and Stop scan
        startAndStopScanMethod();

        //set adapter
        //recycler view
        bluetoothDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bluetoothDeviceRecyclerView.setAdapter(getScanResultAdapter());

        //cancel button Method
        cancelButtonMethod();

        return view;
    }

    private void startAndStopScanMethod() {
        if (isScanning) {
            stopPeriodicScan();
        } else {
            startPeriodicScan();
        }


    }

    private void startPeriodicScan() {
        scanTimer = new Timer();
        scanTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (isScanning) {
                            stopBleScan();
                        } else {
                            startBleScan();
                        }
                    }
                });
            }
        }, 0, SCAN_INTERVAL);
    }

    private void stopPeriodicScan() {
        if (scanTimer != null) {
            scanTimer.cancel();
            scanTimer = null;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                stopBleScan();
            }
        });
    }

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

    public boolean hasPermission(String permissionType) {
        return ContextCompat.checkSelfPermission(requireContext(), permissionType) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void startBleScan() {
        if (!hasRequiredRuntimePermissions()) {
            requestRelevantRuntimePermissions();
        } else {
            Log.d(TAG, "startBleScan");
            scanResults.clear();
            scanResultAdapter.notifyDataSetChanged();

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    BluetoothUtil.requestBluetoothScanPermission(requireActivity());
                }

            }
            bluetoothLeScanner.startScan(null, scanSettings, scanCallback);
            isScanning = true;

        }
    }


    @SuppressLint("NewApi")
    private void requestRelevantRuntimePermissions() {
        if (hasRequiredRuntimePermissions()) {

            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            BluetoothUtil.requestBluetoothEnable(requireActivity(), requireContext());
            BluetoothUtil.requestBluetoothConnectPermission(requireActivity());
            BluetoothUtil.requestBluetoothScanPermission(requireActivity());
            Log.d(TAG, "requestRelevantRuntimePermissions: 171");


        } else {
            LocationUtil.requestFineLocationConnectPermission(requireActivity());
            LocationUtil.requestLocationEnable(requireActivity());
        }

    }

    private void stopBleScan() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d(TAG, "stopBleScan: ");
        bluetoothLeScanner.stopScan(scanCallback);
        isScanning = false;
    }

    private BluetoothAdapter getBluetoothAdapter() {
        if (bluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) requireContext().getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
        return bluetoothAdapter;
    }

    private BluetoothLeScanner getBluetoothLeScanner() {
        return getBluetoothAdapter().getBluetoothLeScanner();
    }

    private ScanResultAdapter getScanResultAdapter() {
        if (scanResultAdapter == null) {
            scanResultAdapter = new ScanResultAdapter(scanResults, new ScanResultAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ScanResult item) {
                    BluetoothDevice device = item.getDevice();
                    Log.w("ScanResultAdapter", "Connecting to " + device.getAddress());
                    @SuppressLint("MissingPermission")
                    BluetoothGatt gatt = device.connectGatt(context, false, new MyBluetoothGattCallback());
                }
            });
        }
        return scanResultAdapter;
    }


    private void cancelButtonMethod() {
        cancelButton.setOnClickListener(view -> dismiss());
    }

    private void idAssigningMethod(View view) {
        cancelButton = view.findViewById(R.id.cancelButton);
        bluetoothDeviceRecyclerView = view.findViewById(R.id.bluetoothDeviceRecyclerView);
        linearLayoutAvailableDevices = view.findViewById(R.id.linearLayoutAvailableDevices);
    }

    private ScanCallback scanCallback = new ScanCallback() {

        @SuppressLint("MissingPermission")
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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scanResultAdapter.notifyItemChanged(indexQuery);
                    }
                });
            } else {
//                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
                @SuppressLint("MissingPermission")
                String deviceName = result.getDevice().getName() != null ? result.getDevice().getName() : "Unnamed";
//              Log.i("ScanCallback", "Found BLE device! Name: " + deviceName + ", address: " + result.getDevice().getAddress());
                if (result.getDevice().getName() != null) {
                    Log.i("ScanCallback", "Found BLE device! Name: " + result.getDevice().getName());
                    scanResults.add(result);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (scanResults.isEmpty()) {
                            scanResultAdapter.notifyItemInserted(scanResults.size() - 1);
                        }
                    }
                });
            }

        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("ScanCallback", "onScanFailed: code " + errorCode);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean containsPermanentDenial = false;
                boolean containsDenial = false;
                boolean allGranted = true;

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permissions[i])) {
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
                    Toast.makeText(requireContext(), "containsPermanentDenial", Toast.LENGTH_SHORT).show();
                } else if (containsDenial) {
                    requestRelevantRuntimePermissions();
                } else if (allGranted && hasRequiredRuntimePermissions()) {
                    startBleScan();
                } else {
                    // Unexpected scenario encountered when handling permissions
                    recreate(requireActivity());
                }
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == REQUEST_ENABLE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean containsPermanentDenial = false;
                boolean containsDenial = false;
                boolean allGranted = true;

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permissions[i])) {
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
                    Toast.makeText(requireContext(), "containsPermanentDenial", Toast.LENGTH_SHORT).show();
                } else if (containsDenial) {
                    requestRelevantRuntimePermissions();
                } else if (allGranted && hasRequiredRuntimePermissions()) {
                    startBleScan();
                } else {
                    // Unexpected scenario encountered when handling permissions
                    recreate(requireActivity());
                }
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}