package com.example.healthcare;

import static com.example.healthcare.BleDevices.BloodGlucometer.*;
import static com.example.healthcare.BleDevices.ECGMeter.ecgDisconnectDeviceMethod;
import static com.example.healthcare.BleDevices.UrionBp.*;
import static com.example.healthcare.BleDevices.WeightScale.*;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.deviceConnected;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.disconnectAllDevices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.healthcare.BleDevices.ECGMeter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class DeviceInfoActivity extends AppCompatActivity {
    public static boolean isDeviceInfoActivityRunning = false;
    private static final String TAG = "TAGi";
    private boolean hasAlertDialogShown = false;
    private boolean isExitForBackPress = false;

    private ProgressBar circularProgressBarBloodGlucometer, circularProgressBarWeightScale, circularProgressBarBloodPressure;
    ImageView backButton;
    String deviceName;
    CardView systolicDiastolicLayout;
    public LinearLayout linearLayoutUrionBp, linearLayoutWeightScale, linearLayoutBloodGlucometer, linearLayoutEcgMeter, messageResultLayout;
    TextView isConnectedTextView;
    TextView systolicReadingTextView, diastolicReadingTextView, pulseReadingTextView, deviceNameTextView, errorMessageTextView;
    TextView weightScaleReadings;
    TextView bloodGlucometerReadingsValue;
    TextView ecgReadings;
    TextView resultTextViewForMessage;


    public static Boolean WEIGHT_SCALE_READING_ALERT_SUCESSFULL = false;
    public static Boolean BLOOD_GLUCOMETER_READING_ALERT_SUCESSFULL = false;
    public static Boolean BLOOD_GLUCOMETER_READING_ALERT_ERROR = false;
    public static Boolean BLOOD_PRESSURE_READING_ALERT_ERROR = false;
    public static Boolean BLOOD_PRESSURE_READING_ALERT_SUCESSFULL = false;
    public static Boolean ECG_READING_ALERT_SUCESSFULL = false;


    private final Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // Update UI here
            // This code will be executed every 0.5 second
            refresh();
            mHandler.postDelayed(this, 500);
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

        //get device name from Intent
        deviceName = getIntent().getStringExtra("DEVICE_NAME");


        //back Button Method
        backButtonConfirmationDialogMethod(this);

    }


    @SuppressLint("SetTextI18n")
    private void refresh() {
        if (deviceName != null) {
            if (deviceName.equals(URION_BP_DEVICE_NAME)) {
                deviceNameTextView.setText("BLOOD PRESSURE");
                urionBpRefresh();               //for Urion Bp
            } else if (deviceName.equals(WEIGHT_SCALE_DEVICE_NAME)) {
                deviceNameTextView.setText("WEIGHT SCALE");
                weightScaleRefresh();             //for Weight Scale
            } else if (deviceName.equals(BLOOD_GLUCOMETER_DEVICE_NAME1) || deviceName.equals(BLOOD_GLUCOMETER_DEVICE_NAME2)) {
                deviceNameTextView.setText("BLOOD GLUCOMETER");
                bloodGlucometerRefresh();             //for Blood Glucometer
            } else if (deviceName.equals(ECGMeter.ECG_DEVICE_NAME1) || deviceName.equals(ECGMeter.ECG_DEVICE_NAME2)) {
                deviceNameTextView.setText("ECG");
                ecgMeterRefresh();             //for ECG meter
            } else {
                deviceNameTextView.setText(deviceName);
            }
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
        messageResultLayout.setVisibility(View.VISIBLE);

        if (deviceConnected) {
            isConnectedTextView.setText("Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.green));

            if (BLOOD_GLUCOMETER_RESULT != null) {
                resultTextViewForMessage.setText(BLOOD_GLUCOMETER_RESULT);

                if (BLOOD_GLUCOMETER_READING_ALERT_ERROR) {

                    alertDialogMethod("The Blood Glucometer has been measured Failed.","---","mg/dl",false);
                    BLOOD_GLUCOMETER_READING_ALERT_ERROR = false;
                }
            }
            if (BLOOD_GLUCOMETER_RESULT_VALUE != null) {
                bloodGlucometerReadingsValue.setText(BLOOD_GLUCOMETER_RESULT_VALUE);

                if (BLOOD_GLUCOMETER_READING_ALERT_SUCESSFULL) {
                    alertDialogMethod("The Blood Pressure has been measured Sucessfully.",BLOOD_GLUCOMETER_RESULT_VALUE,"mg/dl",true);
                    BLOOD_GLUCOMETER_READING_ALERT_SUCESSFULL = false;
                }

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
        messageResultLayout.setVisibility(View.GONE);


        if (WEIGHT_SCALE_IS_CONNECTED) {
            isConnectedTextView.setText("Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.green));

            if (WEIGHT_SCALE_READING != null) {
                String floatConversionReading = String.valueOf(WEIGHT_SCALE_READING);
                weightScaleReadings.setText(floatConversionReading);


                //sucessfull alert here
                if (WEIGHT_SCALE_READING_ALERT_SUCESSFULL) {
                    alertDialogMethod("The Weight has been measured Sucessfully.", String.valueOf(WEIGHT_SCALE_READING),"Kilogram",true);
                    WEIGHT_SCALE_READING_ALERT_SUCESSFULL = false;
                }

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
        messageResultLayout.setVisibility(View.VISIBLE);
        systolicDiastolicLayout.setVisibility(View.VISIBLE);


        if (deviceConnected) {
            isConnectedTextView.setText("Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.green));

            //logic
            resultTextViewForMessage.setText(DEVICE_INFO_CLASS_SET_TEXT);

            if (URION_BP_DIASTOLIC_READINGS != null) {
                diastolicReadingTextView.setText("" + URION_BP_DIASTOLIC_READINGS);
            }

            if (URION_BP_SYSTOLIC_READINGS != null && URION_BP_PULSE_READINGS != null) {
                systolicReadingTextView.setText("" + URION_BP_SYSTOLIC_READINGS);
                pulseReadingTextView.setText("" + URION_BP_PULSE_READINGS);
                resultTextViewForMessage.setVisibility(View.GONE);

                if (BLOOD_PRESSURE_READING_ALERT_SUCESSFULL) {
                    alertDialogMethod( "The Blood pressure has been measured Sucessfully.",URION_BP_SYSTOLIC_READINGS+"\n"+URION_BP_DIASTOLIC_READINGS,"mmHg",false);
                    BLOOD_PRESSURE_READING_ALERT_SUCESSFULL = false;
                }

            }

            if (URION_BP_DEVICE_ERROR_MESSAGES != null) {
                errorMessageTextView.setVisibility(View.VISIBLE);
                systolicReadingTextView.setVisibility(View.GONE);
                diastolicReadingTextView.setVisibility(View.GONE);
                pulseReadingTextView.setVisibility(View.GONE);
                errorMessageTextView.setVisibility(View.GONE);
                resultTextViewForMessage.setText(URION_BP_DEVICE_ERROR_MESSAGES);

                if (BLOOD_PRESSURE_READING_ALERT_ERROR) {
                    alertDialogMethod("The Blood pressure has been measured Failed.","---","mmHg",true);
                    BLOOD_PRESSURE_READING_ALERT_ERROR = false;
                }
            }
        } else {
            isConnectedTextView.setText("Not Connected");
            isConnectedTextView.setTextColor(getResources().getColor(R.color.red));
            messageResultLayout.setVisibility(View.GONE);
        }


    }


    private void idAssignHere() {
        systolicReadingTextView = findViewById(R.id.systolicReading);
        diastolicReadingTextView = findViewById(R.id.diastolicReading);
        pulseReadingTextView = findViewById(R.id.pulseReading);
        errorMessageTextView = findViewById(R.id.errorMessage);
        backButton = findViewById(R.id.deviceInfoBackButton);
        deviceNameTextView = findViewById(R.id.deviceNameTextView);
        linearLayoutUrionBp = findViewById(R.id.linearLayoutUrionBp);
        weightScaleReadings = findViewById(R.id.weightScaleReadings);
        linearLayoutWeightScale = findViewById(R.id.linearLayoutWeightScale);
        isConnectedTextView = findViewById(R.id.isConnectedTextView);
        linearLayoutBloodGlucometer = findViewById(R.id.linearLayoutBloodGlucometer);
        resultTextViewForMessage = findViewById(R.id.resultTextViewForMessage);
        bloodGlucometerReadingsValue = findViewById(R.id.bloodGlucometerReadingsValue);
        ecgReadings = findViewById(R.id.ecgReadings);
        linearLayoutEcgMeter = findViewById(R.id.linearLayoutEcgMeter);
        messageResultLayout = findViewById(R.id.messageResultLayout);
        systolicDiastolicLayout = findViewById(R.id.systolicDiastolicLayout);

        circularProgressBarBloodGlucometer = findViewById(R.id.circularProgressBarBloodGlucometer);
        circularProgressBarWeightScale = findViewById(R.id.circularProgressBarWeightScale);
        circularProgressBarBloodPressure = findViewById(R.id.circularProgressBarBloodPressure);
    }

    private void backButtonMethod() {

        //below logic to turn off the BLE device
        if (deviceName != null) {
            if (deviceName.equals(URION_BP_DEVICE_NAME)) {
                urionBpDisconnectDeviceMethod();         //for Urion Bp
                URION_BP_SYSTOLIC_READINGS = null;
                URION_BP_DIASTOLIC_READINGS = null;
                URION_BP_PULSE_READINGS = null;
            } else if (deviceName.equals(ECGMeter.ECG_DEVICE_NAME1) || deviceName.equals(ECGMeter.ECG_DEVICE_NAME2)) {
                ecgDisconnectDeviceMethod();             //for ECG meter
            } else if (deviceName.equals(WEIGHT_SCALE_DEVICE_NAME)) {
                WEIGHT_SCALE_READING = null;
            } else if (deviceName.equals(BLOOD_GLUCOMETER_DEVICE_NAME1) || deviceName.equals(BLOOD_GLUCOMETER_DEVICE_NAME2)) {
                BLOOD_GLUCOMETER_RESULT = null;
                WEIGHT_SCALE_IS_CONNECTED = false;
            }

        } else {
            Log.d(TAG, "refresh: Device name null");
        }

        //back button
        getOnBackPressedDispatcher().onBackPressed();
    }

    //method ask confirmation for exit , when Click back button
    public void backButtonConfirmationDialogMethod(Context context) {
        backButton.setOnClickListener(v -> {
            showCustomDialogBox("Are you sure want tot Exit?");
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

        //below logic to turn off the BLE device
        if (deviceName != null) {
            if (deviceName.equals(URION_BP_DEVICE_NAME)) {
                urionBpDisconnectDeviceMethod();         //for Urion Bp
            } else if (deviceName.equals(ECGMeter.ECG_DEVICE_NAME1) || deviceName.equals(ECGMeter.ECG_DEVICE_NAME2)) {
                ecgDisconnectDeviceMethod();             //for ECG meter
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


    // alert dialog here
    private void alertDialogMethod(String message, String reading, String unit, boolean isError) {
        if (!hasAlertDialogShown) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ConstraintLayout readingConstraintLayout = dialog.findViewById(R.id.readingConstraintLayout);
            TextView readingValue = dialog.findViewById(R.id.readingValue);
            TextView unitValue = dialog.findViewById(R.id.unitValue);
            TextView tvMessage = dialog.findViewById(R.id.tvMessage);
            Button btnYes = dialog.findViewById(R.id.btnYes);
            Button btnNo = dialog.findViewById(R.id.btnNo);

            if(isError){
                readingConstraintLayout.setVisibility(View.GONE);
                tvMessage.setTextColor(getResources().getColor(R.color.red));
            }else{
                readingConstraintLayout.setVisibility(View.VISIBLE);
            }
            btnNo.setVisibility(View.GONE);
            btnYes.setText("OK");

            tvMessage.setText(message);
            readingValue.setText(reading);
            unitValue.setText(unit);

            btnYes.setOnClickListener(v -> {
                DeviceInfoActivity.super.onBackPressed();
                backButtonMethod();
            });

            dialog.show();

            hasAlertDialogShown = true;
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        showCustomDialogBox("Are you sure want to Exit?");

    }

    //Custom Dialog
    private void showCustomDialogBox(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);

        tvMessage.setText(message);

        btnYes.setOnClickListener(v -> {
            DeviceInfoActivity.super.onBackPressed();
            backButtonMethod();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}




