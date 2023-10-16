package com.example.healthcare;

import static com.example.healthcare.BleDevices.BloodGlucometer.*;
import static com.example.healthcare.BleDevices.UrionBp.*;
import static com.example.healthcare.BleDevices.WeightScale.*;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.deviceConnected;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.healthcare.BluetoothModule.MyBluetoothGattCallback;


public class DeviceInfoActivity extends AppCompatActivity {
    ImageView backButton;
    Handler handler = new Handler();
    Runnable runnable;
    String deviceName;
    public LinearLayout linearLayoutUrionBp, linearLayoutWeightScale, linearLayoutBloodGlucometer;
    TextView isConnectedTextView;
    TextView systolicReadingTextView, diastolicReadingTextView, pulseReadingTextView, deviceNameTextView, deviceInfoTextView, errorMessageTextView;
    TextView weightScaleReadings;
    TextView bloodGlucometerReadings,bloodGlucometerReadingsDateTime;


    private BackgroundTask backgroundTask;


    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                if (isCancelled()) {
                    break;
                }
                publishProgress(); // This will call onProgressUpdate()
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            refresh();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        //Assign Id Here
        idAssignHere();

        // Start background task
        backgroundTask = new BackgroundTask();
        backgroundTask.execute();

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
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private void bloodGlucometerRefresh() {
        linearLayoutUrionBp.setVisibility(View.GONE);
        linearLayoutWeightScale.setVisibility(View.GONE);
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
    }

    private void backButtonMethod() {
        backButton.setOnClickListener(v -> {
            onBackPressed();
            backgroundTask.cancel(true);
            MyBluetoothGattCallback myBluetoothGattCallback = new MyBluetoothGattCallback(getApplicationContext());
            myBluetoothGattCallback.disconnectDevice();
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundTask.cancel(true);
        MyBluetoothGattCallback myBluetoothGattCallback = new MyBluetoothGattCallback(getApplicationContext());
        myBluetoothGattCallback.disconnectDevice();

    }

    private void startAutoRefresh() {
        handler.postDelayed(runnable, 1000);
    }

    private void stopAutoRefresh() {
        handler.removeCallbacks(runnable);
    }


}


