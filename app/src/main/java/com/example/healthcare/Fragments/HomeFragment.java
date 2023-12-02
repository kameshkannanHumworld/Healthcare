package com.example.healthcare.Fragments;

import static com.example.healthcare.Animation.Transition.zoomInTransition;
import static com.example.healthcare.BleDevices.BloodGlucometer.*;
import static com.example.healthcare.BleDevices.ECGMeter.*;
import static com.example.healthcare.BleDevices.UrionBp.*;
import static com.example.healthcare.BleDevices.WeightScale.WEIGHT_SCALE_DEVICE_NAME;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.*;
import static com.example.healthcare.DeviceInfoActivity.isDeviceInfoActivityRunning;
import static com.example.healthcare.MainActivity.TAG;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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

    //UI views
    View viewFragment;
    Context context;
    ImageView weighScaleImage, bpMeterImage, ecgMeterImage, glucometerImage;

    //classes
    private BluetoothScanner bluetoothScanner;
    private final Handler scanHandler = new Handler();
    private AnimationLoading animationLoading;

    //datatype
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

    //initial check for required bluetooth and location permission
    private boolean permissionCheckWhenClickDeviceIcon() {
        boolean isLocationEnable = LocationUtil.requestLocationEnable(requireActivity());
        boolean isFineLocationEnable = LocationUtil.requestFineLocationConnectPermission(requireActivity());
        boolean isBleConnectEnable = BluetoothUtil.requestBluetoothConnectPermission(requireActivity());
        boolean isBluetoothEnable = BluetoothUtil.requestBluetoothEnable(requireActivity(), context);
        boolean isBleScanEnable = BluetoothUtil.requestBluetoothScanPermission(requireActivity());

        // Check if the OVERLAY_PERMISSION is granted
//        if (!Settings.canDrawOverlays(requireContext())) {
//            // If not, request it
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireContext().getPackageName()));
//            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
//        }

        //post notifications permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        //check boolean for above method
        if (!isLocationEnable && !isFineLocationEnable) {
            LocationUtil.requestLocationEnableAlert(context);
        } else if (!isBleScanEnable && !isBluetoothEnable && !isBleConnectEnable) {
            BluetoothUtil.requestBluetoothEnableAlert(context);
        } else {
            return isLocationEnable && isFineLocationEnable && isBleScanEnable && isBluetoothEnable && isBleConnectEnable;
        }
        return false;
    }

    private void ImageListenersMethod() {

        //this isDeviceInfoActivityRunning restricts the double intent
        if (!isDeviceInfoActivityRunning) {

            //Weight Scale Listener
            weighScaleImage.setOnClickListener(view -> {
                boolean isAllPermissionGranted = permissionCheckWhenClickDeviceIcon();
                if (isAllPermissionGranted) {
                    bluetoothScanner = new BluetoothScanner(WEIGHT_SCALE_DEVICE_NAME, context);
                    new Thread(() -> bluetoothScanner.startScan()).start(); // background BLE scan
                    Intent intent = new Intent(context, DeviceInfoActivity.class); //page intent
                    intent.putExtra("DEVICE_NAME", WEIGHT_SCALE_DEVICE_NAME);
                    context.startActivity(intent,zoomInTransition(view));
                }
            });

            //BP Meter Listener
            bpMeterImage.setOnClickListener(view -> {
                boolean isAllPermissionGranted = permissionCheckWhenClickDeviceIcon();
                if (isAllPermissionGranted) {
                    bluetoothScanner = new BluetoothScanner(URION_BP_DEVICE_NAME.get(0), context);
                    new Thread(() -> bluetoothScanner.startScan()).start(); // background BLE scan
                    Intent intent = new Intent(context, DeviceInfoActivity.class); //page intent
                    intent.putExtra("DEVICE_NAME", URION_BP_DEVICE_NAME.get(0));
                    context.startActivity(intent,zoomInTransition(view));
                }
            });

            //BP Meter Listener
            ecgMeterImage.setOnClickListener(view -> {
                boolean isAllPermissionGranted = permissionCheckWhenClickDeviceIcon();
                if (isAllPermissionGranted) {
                    bluetoothScanner = new BluetoothScanner(ECG_DEVICE_NAME.get(0), context);
                    new Thread(() -> bluetoothScanner.startScan()).start(); // background BLE scan
                    Intent intent = new Intent(context, DeviceInfoActivity.class); //page intent
                    intent.putExtra("DEVICE_NAME", ECG_DEVICE_NAME.get(0));
                    context.startActivity(intent,zoomInTransition(view));
                }
            });

            //BP Meter Listener
            glucometerImage.setOnClickListener(view -> {
                boolean isAllPermissionGranted = permissionCheckWhenClickDeviceIcon();
                if (isAllPermissionGranted) {
                    bluetoothScanner = new BluetoothScanner(BLOOD_GLUCOMETER_DEVICE_NAME.get(0), context);
                    new Thread(() -> bluetoothScanner.startScan()).start(); // background BLE scan
                    Intent intent = new Intent(context, DeviceInfoActivity.class); //page intent
                    intent.putExtra("DEVICE_NAME", BLOOD_GLUCOMETER_DEVICE_NAME.get(0));
                    context.startActivity(intent,zoomInTransition(view));
                }
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
        fab.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
//            if (Settings.canDrawOverlays(requireContext())) {
//                // Permission granted
//                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
//            } else {
//                // Permission denied
//                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


}