package com.example.healthcare;

import static com.example.healthcare.BleDevices.UrionBp.*;
import static com.example.healthcare.BluetoothModule.MyBluetoothGattCallback.*;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class DeviceInfoActivity extends AppCompatActivity {
    ImageView backButton;
    Handler handler = new Handler();
    Runnable runnable;
    String deviceName;
    TextView systolicReadingTextView, diastolicReadingTextView, pulseReadingTextView, deviceNameTextView, deviceInfoTextView;



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
                    Thread.sleep(1000); // Sleep for 1 second
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
        deviceNameTextView.setText(getIntent().getStringExtra("DEVICE_NAME"));

        //Assign Readings Value here
//        assignReadingsValueHere();

        //back Button Method
        backButtonMethod();
    }

    private void refreshMethod() {

        runnable = new Runnable() {
            @Override
            public void run() {
                refresh();
                handler.postDelayed(this, 1000); // Refresh every 1000 milliseconds (1 second)
            }
        };
        startAutoRefresh();

    }

    private void startAutoRefresh() {
        handler.postDelayed(runnable, 1000); // Start the auto-refresh runnable
    }

    private void stopAutoRefresh() {
        handler.removeCallbacks(runnable); // Stop the auto-refresh runnable
    }

    @SuppressLint("SetTextI18n")
    private void refresh() {
        deviceInfoTextView.setText(DEVICE_INFO_CLASS_SET_TEXT);
        if (URION_BP_SYSTOLIC_READINGS != null && URION_BP_DIASTOLIC_READINGS != null && URION_BP_PULSE_READINGS != null) {
            Log.d("TAGi", "refresh: ");
            systolicReadingTextView.setText("Systolic reading: " + URION_BP_SYSTOLIC_READINGS);
            diastolicReadingTextView.setText("Diastolic reading: " + URION_BP_DIASTOLIC_READINGS);
            pulseReadingTextView.setText("Pulse reading: " + URION_BP_PULSE_READINGS);
            deviceInfoTextView.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void assignReadingsValueHere() {
        if (URION_BP_SYSTOLIC_READINGS != null && URION_BP_DIASTOLIC_READINGS != null && URION_BP_PULSE_READINGS != null) {
            systolicReadingTextView.setText("Systolic reading: " + URION_BP_SYSTOLIC_READINGS);
            diastolicReadingTextView.setText("Diastolic reading: " + URION_BP_DIASTOLIC_READINGS);
            pulseReadingTextView.setText("Pulse reading: " + URION_BP_PULSE_READINGS);
        } else {
            systolicReadingTextView.setVisibility(View.GONE);
            diastolicReadingTextView.setVisibility(View.GONE);
            pulseReadingTextView.setVisibility(View.GONE);
        }
    }

    private void idAssignHere() {
        systolicReadingTextView = findViewById(R.id.systolicReading);
        diastolicReadingTextView = findViewById(R.id.diastolicReading);
        pulseReadingTextView = findViewById(R.id.pulseReading);
        backButton = findViewById(R.id.deviceInfoBackButton);
        deviceNameTextView = findViewById(R.id.deviceNameTextView);
        deviceInfoTextView = findViewById(R.id.deviceInfoTextView);
    }

    private void backButtonMethod() {
        backButton.setOnClickListener(v -> {
            onBackPressed();
            backgroundTask.cancel(true);
        });
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundTask.cancel(true);
    }




}