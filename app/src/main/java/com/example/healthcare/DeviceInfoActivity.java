package com.example.healthcare;

import static com.example.healthcare.BleDevices.BloodGlucometer.BLOOD_GLUCOMETER_DEVICE_NAME;
import static com.example.healthcare.BleDevices.BloodGlucometer.BLOOD_GLUCOMETER_RESULT;
import static com.example.healthcare.BleDevices.BloodGlucometer.BLOOD_GLUCOMETER_RESULT_VALUE;
import static com.example.healthcare.BleDevices.ECGMeter.ECG_DEVICE_NAME;
import static com.example.healthcare.BleDevices.UrionBp.DEVICE_INFO_CLASS_SET_TEXT;
import static com.example.healthcare.BleDevices.UrionBp.URION_BP_DEVICE_ERROR_MESSAGES;
import static com.example.healthcare.BleDevices.UrionBp.URION_BP_DEVICE_NAME;
import static com.example.healthcare.BleDevices.UrionBp.URION_BP_DIASTOLIC_READINGS;
import static com.example.healthcare.BleDevices.UrionBp.URION_BP_PULSE_READINGS;
import static com.example.healthcare.BleDevices.UrionBp.URION_BP_SYSTOLIC_READINGS;
import static com.example.healthcare.BleDevices.WeightScale.WEIGHT_SCALE_DEVICE_NAME;
import static com.example.healthcare.BleDevices.WeightScale.WEIGHT_SCALE_IS_CONNECTED;
import static com.example.healthcare.BleDevices.WeightScale.WEIGHT_SCALE_READING;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.deviceConnected;
import static com.example.healthcare.BluetoothModule.BluetoothScanner.disconnectAllDevices;
import static com.example.healthcare.MainActivity.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.healthcare.BluetoothModule.BluetoothScanner;
import com.google.android.material.snackbar.Snackbar;


public class DeviceInfoActivity extends AppCompatActivity {
    
    //UI views
    ImageView backButton;
    CardView systolicDiastolicLayout;
    public LinearLayout linearLayoutUrionBp, linearLayoutWeightScale, linearLayoutBloodGlucometer, linearLayoutEcgMeter, messageResultLayout, scanlottieLayout;
    TextView isConnectedTextView;
    TextView systolicReadingTextView, diastolicReadingTextView, pulseReadingTextView, deviceNameTextView, errorMessageTextView;
    TextView weightScaleReadings;
    TextView bloodGlucometerReadingsValue;
    TextView ecgReadings;
    TextView resultTextViewForMessage;
    private LottieAnimationView deviceInfoScanLottie;
    private Context context;
    
    //Datatypes
    String deviceName;
    public static boolean isDeviceInfoActivityRunning = false;
    private boolean hasAlertDialogShown = false;
    public static Boolean WEIGHT_SCALE_READING_ALERT_SUCESSFULL = false;
    public static Boolean BLOOD_GLUCOMETER_READING_ALERT_SUCESSFULL = false;
    public static Boolean BLOOD_GLUCOMETER_READING_ALERT_ERROR = false;
    public static Boolean BLOOD_PRESSURE_READING_ALERT_ERROR = false;
    public static Boolean BLOOD_PRESSURE_READING_ALERT_SUCESSFULL = false;

    //Classes
    private BluetoothScanner bluetoothScanner;
    private final Handler mHandler = new Handler();

    //background refresh 
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

        //lottie auto replay
        deviceInfoScanLottie.setRepeatCount(LottieDrawable.INFINITE);
        deviceInfoScanLottie.playAnimation();

        //device not found - snackbar
        context = this;
        new Handler().postDelayed(() -> {
            if (!deviceConnected) {
                View view = findViewById(android.R.id.content);
                if (view != null) {
                    scanlottieLayout.setVisibility(View.GONE);
                    Snackbar.make(view, "Device Not Found, Please try again..", Snackbar.LENGTH_SHORT)
                            .setAction("Retry", v -> {
                                bluetoothScanner = new BluetoothScanner(deviceName, context);
                                bluetoothScanner.startScan();
                                scanlottieLayout.setVisibility(View.VISIBLE);
                            })
                            .setDuration(5000)
                            .show();
                }
            }
        }, 30000);


        //back Button Method
        backButtonConfirmationDialogMethod();

    }


    //this method will refresh every 500 milliseconds
    @SuppressLint("SetTextI18n")
    private void refresh() {
        if (deviceName != null) {
            if (deviceName.equals(URION_BP_DEVICE_NAME.get(0))) {
                deviceNameTextView.setText("Blood Pressure");
                urionBpRefresh();               //for Urion Bp
            } else if (deviceName.equals(WEIGHT_SCALE_DEVICE_NAME)) {
                deviceNameTextView.setText("Weight Scale");
                weightScaleRefresh();             //for Weight Scale
            } else if (BLOOD_GLUCOMETER_DEVICE_NAME.contains(deviceName)) {
                deviceNameTextView.setText("Blood Glucometer");
                bloodGlucometerRefresh();             //for Blood Glucometer
            } else if (ECG_DEVICE_NAME.contains(deviceName)) {
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
            isConnectedTextView.setTextColor(ContextCompat.getColor(context,R.color.green));
            isConnectedTextView.setTextColor(ContextCompat.getColor(context,R.color.green));

            //logic here


        } else {
            isConnectedTextView.setText("Not Connected");
            isConnectedTextView.setTextColor(ContextCompat.getColor(context,R.color.red));
        }
    }

    @SuppressLint("SetTextI18n")
    private void bloodGlucometerRefresh() {
        linearLayoutUrionBp.setVisibility(View.GONE);
        linearLayoutWeightScale.setVisibility(View.GONE);
        linearLayoutEcgMeter.setVisibility(View.GONE);


        if (deviceConnected) {
            deviceInfoScanLottie.setVisibility(View.GONE);
            linearLayoutBloodGlucometer.setVisibility(View.VISIBLE);
            messageResultLayout.setVisibility(View.VISIBLE);


            isConnectedTextView.setText("Connected");
            isConnectedTextView.setTextColor(ContextCompat.getColor(this, R.color.green));

            if (BLOOD_GLUCOMETER_RESULT != null) {
                resultTextViewForMessage.setText(BLOOD_GLUCOMETER_RESULT);

                if (BLOOD_GLUCOMETER_READING_ALERT_ERROR) {

                    alertDialogMethod("The Blood Glucometer has been measured Failed.", "---", "mg/dl", true, 50);
                    BLOOD_GLUCOMETER_READING_ALERT_ERROR = false;
                }
            }
            if (BLOOD_GLUCOMETER_RESULT_VALUE != null) {
                bloodGlucometerReadingsValue.setText(BLOOD_GLUCOMETER_RESULT_VALUE);

                if (BLOOD_GLUCOMETER_READING_ALERT_SUCESSFULL) {
                    alertDialogMethod("The Blood Glucometer has been measured Sucessfully.", BLOOD_GLUCOMETER_RESULT_VALUE, "mg/dl", false, 50);
                    BLOOD_GLUCOMETER_READING_ALERT_SUCESSFULL = false;
                }

            }
        } else {
            isConnectedTextView.setText("Not Connected");
            isConnectedTextView.setTextColor(ContextCompat.getColor(context,R.color.red));

            deviceInfoScanLottie.setVisibility(View.VISIBLE);
            linearLayoutBloodGlucometer.setVisibility(View.GONE);
            messageResultLayout.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void weightScaleRefresh() {
        linearLayoutUrionBp.setVisibility(View.GONE);
        linearLayoutBloodGlucometer.setVisibility(View.GONE);
        linearLayoutEcgMeter.setVisibility(View.GONE);
        messageResultLayout.setVisibility(View.GONE);


        if (WEIGHT_SCALE_IS_CONNECTED) {
            deviceConnected = true;
            deviceInfoScanLottie.setVisibility(View.GONE);
            linearLayoutWeightScale.setVisibility(View.VISIBLE);

            isConnectedTextView.setText("Connected");
            isConnectedTextView.setTextColor(ContextCompat.getColor(context,R.color.green));

            if (WEIGHT_SCALE_READING != null) {
                String floatConversionReading = String.valueOf(WEIGHT_SCALE_READING);
                weightScaleReadings.setText(floatConversionReading);


                //sucessfull alert here
                if (WEIGHT_SCALE_READING_ALERT_SUCESSFULL) {
                    alertDialogMethod("The Weight has been measured Sucessfully.", String.valueOf(WEIGHT_SCALE_READING), "Kilogram", false, 50);
                    WEIGHT_SCALE_READING_ALERT_SUCESSFULL = false;
                    deviceConnected = false;
                }

            }
        } else {
            isConnectedTextView.setText("Not Connected");
            isConnectedTextView.setTextColor(ContextCompat.getColor(context,R.color.red));

            deviceInfoScanLottie.setVisibility(View.VISIBLE);
            linearLayoutWeightScale.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void urionBpRefresh() {
        linearLayoutWeightScale.setVisibility(View.GONE);
        linearLayoutBloodGlucometer.setVisibility(View.GONE);
        linearLayoutEcgMeter.setVisibility(View.GONE);


        if (deviceConnected) {
            deviceInfoScanLottie.setVisibility(View.GONE);
            linearLayoutUrionBp.setVisibility(View.VISIBLE);
            messageResultLayout.setVisibility(View.VISIBLE);
            systolicDiastolicLayout.setVisibility(View.VISIBLE);

            isConnectedTextView.setText("Connected");
            isConnectedTextView.setTextColor(ContextCompat.getColor(context,R.color.green));

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
                    alertDialogMethod("The Blood pressure has been measured Sucessfully.", URION_BP_SYSTOLIC_READINGS + "/" + URION_BP_DIASTOLIC_READINGS, "mmHg", false, 30);
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
                    alertDialogMethod("The Blood pressure has been measured Failed.", "---", "mmHg", true, 50);
                    BLOOD_PRESSURE_READING_ALERT_ERROR = false;
                }
            }
        } else {
            isConnectedTextView.setText("Not Connected");
            isConnectedTextView.setTextColor(ContextCompat.getColor(context,R.color.red));

            deviceInfoScanLottie.setVisibility(View.VISIBLE);
            messageResultLayout.setVisibility(View.GONE);
            linearLayoutUrionBp.setVisibility(View.GONE);
            systolicDiastolicLayout.setVisibility(View.GONE);
        }


    }

    //Assign ID for the UI views
    private void idAssignHere() {
        deviceInfoScanLottie = findViewById(R.id.deviceInfoScanLottie);
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
        scanlottieLayout = findViewById(R.id.scanlottieLayout);
        resultTextViewForMessage = findViewById(R.id.resultTextViewForMessage);
        bloodGlucometerReadingsValue = findViewById(R.id.bloodGlucometerReadingsValue);
        ecgReadings = findViewById(R.id.ecgReadings);
        linearLayoutEcgMeter = findViewById(R.id.linearLayoutEcgMeter);
        messageResultLayout = findViewById(R.id.messageResultLayout);
        systolicDiastolicLayout = findViewById(R.id.systolicDiastolicLayout);

    }

    //back button logic here
    private void backButtonMethod() {

        //below logic to turn off the BLE device
        if (deviceName != null) {
            if (deviceName.equals(URION_BP_DEVICE_NAME.get(0))) {
//                if (deviceConnected) {
//                    urionBpDisconnectDeviceMethod();         //for Urion Bp
//                }
                URION_BP_SYSTOLIC_READINGS = null;
                URION_BP_DIASTOLIC_READINGS = null;
                URION_BP_PULSE_READINGS = null;
            } else if (ECG_DEVICE_NAME.contains(deviceName)) {
//                if (deviceConnected) {
//                    ecgDisconnectDeviceMethod();             //for ECG meter
//                }
                Log.e(TAG, "backButtonMethod ECG working" );
            } else if (deviceName.equals(WEIGHT_SCALE_DEVICE_NAME)) {
                WEIGHT_SCALE_READING = null;
            } else if (BLOOD_GLUCOMETER_DEVICE_NAME.contains(deviceName)) {
                BLOOD_GLUCOMETER_RESULT = null;
                BLOOD_GLUCOMETER_RESULT_VALUE = null;
                WEIGHT_SCALE_IS_CONNECTED = false;
            }

        } else {
            Log.d(TAG, "refresh: Device name null");
        }

        //back button
        getOnBackPressedDispatcher().onBackPressed();
    }

    //method ask confirmation for exit , when Click back button
    public void backButtonConfirmationDialogMethod() {
        backButton.setOnClickListener(v -> showCustomDialogBox("Are you sure want to Exit?"));
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
//        if (deviceName != null) {
//            if (deviceName.equals(URION_BP_DEVICE_NAME)) {
//                if (deviceConnected) {
//                    urionBpDisconnectDeviceMethod();         //for Urion Bp
//                }
//            } else if (ECG_DEVICE_NAME.contains(deviceName) {
//                if(deviceConnected){
//                    ecgDisconnectDeviceMethod();             //for ECG meter
//                }
//            }
//
//        } else {
//            Log.d(TAG, "refresh: Device name null");
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAGi", "onDestroy");

        //stop the scan
        bluetoothScanner = new BluetoothScanner(deviceName, context);
        bluetoothScanner.stopScan();

        // Disconnect the device in onDestroy
        disconnectAllDevices();


    }


    // alert dialog here
    @SuppressLint("SetTextI18n")
    private void alertDialogMethod(String message, String reading, String unit, boolean isError, int textSize) {
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
            TextView dialogHeader = dialog.findViewById(R.id.dialogHeader);


            if (isError) {
                dialogHeader.setText("Failed");
                readingConstraintLayout.setVisibility(View.GONE);
                tvMessage.setTextColor(ContextCompat.getColor(context,R.color.red));
            } else {
                readingConstraintLayout.setVisibility(View.VISIBLE);
                dialogHeader.setText("Sucess");
            }
            btnNo.setVisibility(View.GONE);
            btnYes.setText("OK");

            readingValue.setTextSize(textSize);
            tvMessage.setText(message);
            readingValue.setText(reading);
            unitValue.setText(unit);

            btnYes.setOnClickListener(v -> {
                backButtonMethod();
                dialog.dismiss();
                DeviceInfoActivity.super.onBackPressed();
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
    @SuppressLint("SetTextI18n")
    private void showCustomDialogBox(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        TextView dialogHeader = dialog.findViewById(R.id.dialogHeader);

        dialogHeader.setText("Confirmation");
        tvMessage.setText(message);

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            DeviceInfoActivity.super.onBackPressed();
            backButtonMethod();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


}




