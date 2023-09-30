package com.example.healthcare;

import static com.example.healthcare.BleDevices.UrionBp.*;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class DeviceInfoActivity extends AppCompatActivity {
    ImageView backButton;
    TextView systolicReading, diastolicReading, pulseReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        //Assign Id Here
        idAssignHere();

        //Assign Readings Value here
        assignReadingsValueHere();

        //back Button Method
        backButtonMethod();
    }

    @SuppressLint("SetTextI18n")
    private void assignReadingsValueHere() {
        if (URION_BP_SYSTOLIC_READINGS != null && URION_BP_DIASTOLIC_READINGS != null && URION_BP_PULSE_READINGS != null) {
            systolicReading.setText("Systolic reading: " + URION_BP_SYSTOLIC_READINGS);
            diastolicReading.setText("Diastolic reading: " + URION_BP_DIASTOLIC_READINGS);
            pulseReading.setText("Pulse reading: " + URION_BP_PULSE_READINGS);
        }else{
            systolicReading.setText("Please Take Readings");
            diastolicReading.setVisibility(View.GONE);
            pulseReading.setVisibility(View.GONE);
        }
    }

    private void idAssignHere() {
        systolicReading = findViewById(R.id.systolicReading);
        diastolicReading = findViewById(R.id.diastolicReading);
        pulseReading = findViewById(R.id.pulseReading);
        backButton = findViewById(R.id.deviceInfoBackButton);
    }

    private void backButtonMethod() {
        backButton.setOnClickListener(v -> {
            onBackPressed(); // This will simulate the behavior of the back button press
        });
    }
}