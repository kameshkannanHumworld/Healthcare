package com.example.healthcare.Fragments;

import static com.example.healthcare.BleDevices.BloodGlucometer.*;
import static com.example.healthcare.BleDevices.ECGMeter.*;
import static com.example.healthcare.BleDevices.UrionBp.URION_BP_DEVICE_NAME;
import static com.example.healthcare.BleDevices.WeightScale.WEIGHT_SCALE_DEVICE_NAME;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.*;
import static com.example.healthcare.DeviceInfoActivity.isDeviceInfoActivityRunning;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.healthcare.Animation.AnimationLoading;
import com.example.healthcare.BleDevices.BloodGlucometer;
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
    public static List<BluetoothGatt> connectedGatts = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean isScanning = true;
    private static final String TAG = "TAGi";


    private String mParam1;
    private String mParam2;
    Runnable runnable;
    View viewFragment;
    private BluetoothScanner bluetoothScanner;
    private Handler handler;

    Context context;
    ImageView weighScaleImage, bpMeterImage, ecgMeterImage, glucometerImage;
    private Handler scanHandler = new Handler();
    private AnimationLoading animationLoading;


    public HomeFragment() {

    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Assign Id here
        idAssignMethod(view);
        viewFragment = view;
        animationLoading = new AnimationLoading(getActivity());

        //location and Bluetooth check
        LocationUtil.requestLocationEnable(requireActivity());
        LocationUtil.requestFineLocationConnectPermission(requireActivity());
        BluetoothUtil.requestBluetoothConnectPermission(requireActivity());
        BluetoothUtil.requestBluetoothScanPermission(requireActivity());
        BluetoothUtil.requestBluetoothEnable(requireActivity(), context);

        // Set click listener for the FAB
        floatingActionButtonMethod(view);

        //Image Listeners
        ImageListenersMethod();

        return view;
    }


    private void ImageListenersMethod() {

        //Weight Scale Listener
        if (!isDeviceInfoActivityRunning) {
            weighScaleImage.setOnClickListener(view -> {
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
                bluetoothScanner = new BluetoothScanner(URION_BP_DEVICE_NAME, context);
                animationLoading.startLoadingDialogBlutoothScan();
                startBackgroundScan();
                new Thread(() -> {
                    if (deviceConnected) {
                        animationLoading.dismissLoadingDialog();
                        bluetoothScanner.stopScan();
                    }
                }).start();
            });
        }

        //ECG meter Listener
        if (!isDeviceInfoActivityRunning) {
            ecgMeterImage.setOnClickListener(view -> {
                bluetoothScanner = new BluetoothScanner(ECG_DEVICE_NAME1, context);
                animationLoading.startLoadingDialogBlutoothScan();
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
                bluetoothScanner = new BluetoothScanner(BLOOD_GLUCOMETER_DEVICE_NAME1, context);
                animationLoading.startLoadingDialogBlutoothScan();
                startBackgroundScan();
                new Thread(() -> {
                    if (deviceConnected) {
                        animationLoading.dismissLoadingDialog();
                        bluetoothScanner.stopScan();
                    } else {
                        // If the first device is not connected, try connecting to the second device
                        bluetoothScanner = new BluetoothScanner(BLOOD_GLUCOMETER_DEVICE_NAME1, context);
                        startBackgroundScan(); // Start scanning for the second device
                        if (deviceConnected) {
                            animationLoading.dismissLoadingDialog();
                            bluetoothScanner.stopScan();
                        }
                    }
                }).start();
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


}