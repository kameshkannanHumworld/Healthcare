package com.example.healthcare;

import static com.example.healthcare.BleDevices.BloodGlucometer.*;
import static com.example.healthcare.BleDevices.ECGMeter.ECG_DEVICE_NAME1;
import static com.example.healthcare.BleDevices.ECGMeter.ECG_DEVICE_NAME2;
import static com.example.healthcare.BleDevices.ECGMeter.ecgDisconnectDeviceMethod;
import static com.example.healthcare.BleDevices.UrionBp.*;
import static com.example.healthcare.BleDevices.WeightScale.*;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.deviceConnected;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.disconnectAllDevices;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.healthcare.BleDevices.ECGMeter;

import java.util.Objects;


public class DeviceInfoActivity extends AppCompatActivity {
    public static boolean isDeviceInfoActivityRunning = false;
    private static final String TAG = "TAGi";

    ImageView backButton;
    String deviceName;
    public LinearLayout linearLayoutUrionBp, linearLayoutWeightScale, linearLayoutBloodGlucometer, linearLayoutEcgMeter;
    TextView isConnectedTextView;
    TextView systolicReadingTextView, diastolicReadingTextView, pulseReadingTextView, deviceNameTextView, deviceInfoTextView, errorMessageTextView;
    TextView weightScaleReadings;
    TextView bloodGlucometerReadings, bloodGlucometerReadingsDateTime;
    TextView ecgReadings;


    private final Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // Update UI here
            // This code will be executed every second
            refresh();
            mHandler.postDelayed(this, 500); // Schedule the code to run again in 0.5 second
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        Log.d("TAGi", "onCreate ");

        //prevent
        if (isDeviceInfoActivityRunning) {
            // Activity is already running, you can finish it or do something else.
            finish();
            return;
        } else {
            Log.d(TAG, "onCreate: else part");
        }
        isDeviceInfoActivityRunning = true;


        //Assign Id Here
        idAssignHere();

        //get device name
        deviceName = getIntent().getStringExtra("DEVICE_NAME");
        deviceNameTextView.setText(deviceName);

        //back Button Method
        backButtonMethod();

    }


    @SuppressLint("SetTextI18n")
    private void refresh() {
        if (deviceName != null) {
            if (deviceName.equals(URION_BP_DEVICE_NAME)) {
                urionBpRefresh();               //for Urion Bp
            } else if (deviceName.equals(WEIGHT_SCALE_DEVICE_NAME)) {
                weightScaleRefresh();             //for Weight Scale
            } else if (deviceName.equals(BLOOD_GLUCOMETER_DEVICE_NAME1) || deviceName.equals(BLOOD_GLUCOMETER_DEVICE_NAME2)) {
                bloodGlucometerRefresh();             //for Blood Glucometer
            } else if (deviceName.equals(ECGMeter.ECG_DEVICE_NAME1) || deviceName.equals(ECGMeter.ECG_DEVICE_NAME2)) {
                ecgMeterRefresh();             //for ECG meter
            }
        } else {
            Log.d(TAG, "refresh: Device name null");
        }
    }

    @SuppressLint("SetTextI18n")
    private void ecgMeterRefresh() {
        linearLayoutUrionBp.setVisibility(View.GONE);
        linearLayoutWeightScale.setVisibility(View.GONE);
        linearLayoutBloodGlucometer.setVisibility(View.GONE);
        linearLayoutEcgMeter.setVisibility(View.VISIBLE);

        if (deviceConnected) {
            isConnectedTextView.setText("Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.green));

            //logic here


        } else {
            isConnectedTextView.setText("Not Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.red));
        }
    }

    @SuppressLint("SetTextI18n")
    private void bloodGlucometerRefresh() {
        linearLayoutUrionBp.setVisibility(View.GONE);
        linearLayoutWeightScale.setVisibility(View.GONE);
        linearLayoutEcgMeter.setVisibility(View.GONE);
        linearLayoutBloodGlucometer.setVisibility(View.VISIBLE);

        if (deviceConnected) {
            isConnectedTextView.setText("Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.green));

            if (BLOOD_GLUCOMETER_RESULT != null) {
                bloodGlucometerReadings.setText(BLOOD_GLUCOMETER_RESULT);
            }
            if (BLOOD_GLUCOMETER_RESULT_DATE_TIME != null) {
                bloodGlucometerReadingsDateTime.setText("Date-Time : " + BLOOD_GLUCOMETER_RESULT_DATE_TIME);
            }
        } else {
            isConnectedTextView.setText("Not Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.red));
        }
    }

    @SuppressLint("SetTextI18n")
    private void weightScaleRefresh() {
        linearLayoutUrionBp.setVisibility(View.GONE);
        linearLayoutBloodGlucometer.setVisibility(View.GONE);
        linearLayoutEcgMeter.setVisibility(View.GONE);
        linearLayoutWeightScale.setVisibility(View.VISIBLE);

        if (WEIGHT_SCALE_IS_CONNECTED) {
            isConnectedTextView.setText("Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.green));

            if (WEIGHT_SCALE_READING != null) {
                String floatConversionReading = String.valueOf(WEIGHT_SCALE_READING);
                weightScaleReadings.setText("Weight Scale Reading(Kg): " + floatConversionReading);

            }
        } else {
            isConnectedTextView.setText("Not Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.red));
        }
    }

    @SuppressLint("SetTextI18n")
    private void urionBpRefresh() {
        linearLayoutWeightScale.setVisibility(View.GONE);
        linearLayoutBloodGlucometer.setVisibility(View.GONE);
        linearLayoutEcgMeter.setVisibility(View.GONE);
        linearLayoutUrionBp.setVisibility(View.VISIBLE);


        if (deviceConnected) {
            isConnectedTextView.setText("Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.green));

            //logic
            deviceInfoTextView.setText(DEVICE_INFO_CLASS_SET_TEXT);

            if (URION_BP_DIASTOLIC_READINGS != null) {
                diastolicReadingTextView.setText("Diastolic reading: " + URION_BP_DIASTOLIC_READINGS);
            }

            if (URION_BP_SYSTOLIC_READINGS != null && URION_BP_PULSE_READINGS != null) {
                systolicReadingTextView.setText("Systolic reading: " + URION_BP_SYSTOLIC_READINGS);
                pulseReadingTextView.setText("Pulse reading: " + URION_BP_PULSE_READINGS);
                deviceInfoTextView.setVisibility(View.GONE);
            }

            if (URION_BP_DEVICE_ERROR_MESSAGES != null) {
                errorMessageTextView.setVisibility(View.VISIBLE);
                systolicReadingTextView.setVisibility(View.GONE);
                diastolicReadingTextView.setVisibility(View.GONE);
                pulseReadingTextView.setVisibility(View.GONE);
                deviceInfoTextView.setVisibility(View.GONE);
                errorMessageTextView.setText(URION_BP_DEVICE_ERROR_MESSAGES);
            }
        } else {
            isConnectedTextView.setText("Not Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.red));
            linearLayoutUrionBp.setVisibility(View.GONE);
        }


    }


    private void idAssignHere() {
        systolicReadingTextView = findViewById(R.id.systolicReading);
        diastolicReadingTextView = findViewById(R.id.diastolicReading);
        pulseReadingTextView = findViewById(R.id.pulseReading);
        errorMessageTextView = findViewById(R.id.errorMessage);
        backButton = findViewById(R.id.deviceInfoBackButton);
        deviceNameTextView = findViewById(R.id.deviceNameTextView);
        deviceInfoTextView = findViewById(R.id.deviceInfoTextView);
        linearLayoutUrionBp = findViewById(R.id.linearLayoutUrionBp);
        weightScaleReadings = findViewById(R.id.weightScaleReadings);
        linearLayoutWeightScale = findViewById(R.id.linearLayoutWeightScale);
        isConnectedTextView = findViewById(R.id.isConnectedTextView);
        linearLayoutBloodGlucometer = findViewById(R.id.linearLayoutBloodGlucometer);
        bloodGlucometerReadings = findViewById(R.id.bloodGlucometerReadings);
        bloodGlucometerReadingsDateTime = findViewById(R.id.bloodGlucometerReadingsDateTime);
        ecgReadings = findViewById(R.id.ecgReadings);
        linearLayoutEcgMeter = findViewById(R.id.linearLayoutEcgMeter);
    }

    private void backButtonMethod() {
        backButton.setOnClickListener(v -> {
            if (deviceName != null) {
                if (deviceName.equals(URION_BP_DEVICE_NAME)) {
                    urionBpDisconnectDeviceMethod();         //for Urion Bp
                } else if (deviceName.equals(ECGMeter.ECG_DEVICE_NAME1) || deviceName.equals(ECGMeter.ECG_DEVICE_NAME2)) {
                    ecgDisconnectDeviceMethod();             //for ECG meter
                } else {
                    Log.d(TAG, "refresh: Other Device");
                }

            } else {
                Log.d(TAG, "refresh: Device name null");
            }

            getOnBackPressedDispatcher().onBackPressed();

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mRunnable); // Start the periodic updates when the activity is resumed
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable); // Stop the periodic updates when the activity is paused
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAGi", "onStop");
        isDeviceInfoActivityRunning = false;
        if (deviceName != null) {
            if (deviceName.equals(URION_BP_DEVICE_NAME)) {
                urionBpDisconnectDeviceMethod();         //for Urion Bp
            } else if (deviceName.equals(ECGMeter.ECG_DEVICE_NAME1) || deviceName.equals(ECGMeter.ECG_DEVICE_NAME2)) {
                ecgDisconnectDeviceMethod();             //for ECG meter
            } else {
                Log.d(TAG, "refresh: Other Device");
            }

        } else {
            Log.d(TAG, "refresh: Device name null");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAGi", "onDestroy");

        // Disconnect the device in onDestroy
        disconnectAllDevices();


    }


}


