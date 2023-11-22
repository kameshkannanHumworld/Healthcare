package com.example.healthcare.Fragments;

import static com.example.healthcare.BleDevices.BloodGlucometer.*;
import static com.example.healthcare.BleDevices.ECGMeter.*;
import static com.example.healthcare.BleDevices.UrionBp.URION_BP_DEVICE_NAME;
import static com.example.healthcare.BleDevices.WeightScale.WEIGHT_SCALE_DEVICE_NAME;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.*;
import static com.example.healthcare.DeviceInfoActivity.isDeviceInfoActivityRunning;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.healthcare.Animation.AnimationLoading;
import com.example.healthcare.BluetoothModule.BluetoothScanner;
import com.example.healthcare.BottomSheetDialog.MyBottomSheetDialogFragment;
import com.example.healthcare.DeviceInfoActivity;
import com.example.healthcare.Permissions.BluetoothUtil;
import com.example.healthcare.Permissions.LocationUtil;
import com.example.healthcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    View viewFragment;
    Context context;
    ImageView weighScaleImage, bpMeterImage, ecgMeterImage, glucometerImage;

    private boolean isScanning = true;
    private static final String TAG = "TAGi";
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;


    private BluetoothScanner bluetoothScanner;
    private Handler scanHandler = new Handler();
    private AnimationLoading animationLoading;
    public static List<BluetoothGatt> connectedGatts = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Assign Id here
        idAssignMethod(view);
        viewFragment = view;
        animationLoading = new AnimationLoading(getActivity());

        //location and Bluetooth check
        permissionCheckWhenClickDeviceIcon();

        // Set click listener for the FAB
        floatingActionButtonMethod(view);

        //Image Listeners
        ImageListenersMethod();

        return view;
    }


    private void permissionCheckWhenClickDeviceIcon() {
        LocationUtil.requestLocationEnable(requireActivity());
        LocationUtil.requestFineLocationConnectPermission(requireActivity());
        BluetoothUtil.requestBluetoothConnectPermission(requireActivity());
        BluetoothUtil.requestBluetoothEnable(requireActivity(), context);
        BluetoothUtil.requestBluetoothScanPermission(requireActivity());

        // Check if the OVERLAY_PERMISSION is granted
        if (!Settings.canDrawOverlays(requireContext())) {
            // If not, request it
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireContext().getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }

        //post notifications permission
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }
    }

    private void ImageListenersMethod() {

        //Weight Scale Listener
        if (!isDeviceInfoActivityRunning) {
            weighScaleImage.setOnClickListener(view -> {
                permissionCheckWhenClickDeviceIcon();
                bluetoothScanner = new BluetoothScanner(WEIGHT_SCALE_DEVICE_NAME, context);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothScanner.startScan();
                    }
                }).start();
                //page intent
                Intent intent = new Intent(context, DeviceInfoActivity.class);
                intent.putExtra("DEVICE_NAME", WEIGHT_SCALE_DEVICE_NAME);
                context.startActivity(intent);

            });
        }

        //BP Meter Listener
        if (!isDeviceInfoActivityRunning) {
            bpMeterImage.setOnClickListener(view -> {
                permissionCheckWhenClickDeviceIcon();
                bluetoothScanner = new BluetoothScanner(URION_BP_DEVICE_NAME, context);
//                animationLoading.startLoadingDialogBlutoothScan(URION_BP_DEVICE_NAME, context);
//                startBackgroundScan();
//                new Thread(() -> {
//                    if (deviceConnected) {
//                        animationLoading.dismissLoadingDialog();
//                        bluetoothScanner.stopScan();
//                    }
//                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothScanner.startScan();
                    }
                }).start();
                //page intent
                Intent intent = new Intent(context, DeviceInfoActivity.class);
                intent.putExtra("DEVICE_NAME", URION_BP_DEVICE_NAME);
                context.startActivity(intent);
            });
        }

        //ECG meter Listener
        if (!isDeviceInfoActivityRunning) {
            ecgMeterImage.setOnClickListener(view -> {
                permissionCheckWhenClickDeviceIcon();
                bluetoothScanner = new BluetoothScanner(ECG_DEVICE_NAME1, context);
                animationLoading.startLoadingDialogBlutoothScan(ECG_DEVICE_NAME1, context);
                startBackgroundScan();
                new Thread(() -> {
                    if (deviceConnected) {
                        animationLoading.dismissLoadingDialog();
                        bluetoothScanner.stopScan();
                    } else {
                        // If the first device is not connected, try connecting to the second device
                        bluetoothScanner = new BluetoothScanner(ECG_DEVICE_NAME2, context);
                        startBackgroundScan(); // Start scanning for the second device
                        if (deviceConnected) {
                            animationLoading.dismissLoadingDialog();
                            bluetoothScanner.stopScan();
                        }
                    }
                }).start();
            });
        }

        //Glucometer Listener
        if (!isDeviceInfoActivityRunning) {
            glucometerImage.setOnClickListener(view -> {
                permissionCheckWhenClickDeviceIcon();
                bluetoothScanner = new BluetoothScanner(BLOOD_GLUCOMETER_DEVICE_NAME1, context);
                startBackgroundScan();

                new Thread(() -> {
                    // If the first device is not connected, try connecting to the second device
                    bluetoothScanner = new BluetoothScanner(BLOOD_GLUCOMETER_DEVICE_NAME1, context);
                    startBackgroundScan(); // Start scanning for the second device

                    //page intent
                    Intent intent = new Intent(context, DeviceInfoActivity.class);
                    intent.putExtra("DEVICE_NAME", BLOOD_GLUCOMETER_DEVICE_NAME2);
                    context.startActivity(intent);
                }).start();

                //page intent
                Intent intent = new Intent(context, DeviceInfoActivity.class);
                intent.putExtra("DEVICE_NAME", BLOOD_GLUCOMETER_DEVICE_NAME1);
                context.startActivity(intent);

            });
        }


    }

    //Background Bluetooth scan start
    private void startBackgroundScan() {
        scanHandler.post(() -> {
            if (bluetoothScanner != null) {
                bluetoothScanner.startScan();
            }
        });
    }

    //Assign ID for the UI here
    private void idAssignMethod(View view) {
        weighScaleImage = view.findViewById(R.id.weighScaleImage);
        glucometerImage = view.findViewById(R.id.glucometerImage);
        bpMeterImage = view.findViewById(R.id.bpMeterImage);
        ecgMeterImage = view.findViewById(R.id.ecgMeterImage);
    }

    //floating Action Button Method
    private void floatingActionButtonMethod(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fabHomeFragment);
        fab.setOnClickListener(v -> {
            // Show the bottom sheet dialog
            MyBottomSheetDialogFragment bottomSheetDialogFragment = new MyBottomSheetDialogFragment(context);
            bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
        });
        fab.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onPause() {
        super.onPause();
        animationLoading.dismissLoadingDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: HomeFragment");
        animationLoading.dismissLoadingDialog();
        disconnectAllDevices();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (Settings.canDrawOverlays(requireContext())) {
                // Permission granted
                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}