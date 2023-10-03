package com.example.healthcare;

        import static com.example.healthcare.BleDevices.UrionBp.*;
        import static com.example.healthcare.BluetoothModule.MyBluetoothGattCallback.*;

        import androidx.appcompat.app.AppCompatActivity;

        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.util.Log;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;


public class DeviceInfoActivity extends AppCompatActivity {
    ImageView backButton;
    Handler handler = new Handler();
    Runnable runnable;
    String deviceName;
    public  LinearLayout linearLayoutDeviceInfo;
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
                    Thread.sleep(50); // Sleep for 1 second
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

        //back Button Method
        backButtonMethod();
    }

    public void createTextViews(Context context,  String setTextValue) {

        // Create a new TextView
        TextView textView = new TextView(context);

        // Generate a unique ID for the TextView
        int id = View.generateViewId();
        textView.setId(id);

        // Set text and other attributes
        textView.setText(setTextValue);
        textView.setTextSize(15);

        // Set layout parameters with margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 8, 8, 8);
        textView.setLayoutParams(layoutParams);

        // Add the TextView to the provided LinearLayout
        linearLayoutDeviceInfo.addView(textView);

    }


    @SuppressLint("SetTextI18n")
    private void refresh() {
        deviceInfoTextView.setText(DEVICE_INFO_CLASS_SET_TEXT);
        if (URION_BP_DIASTOLIC_READINGS != null) {
            diastolicReadingTextView.setText("Diastolic reading: " + URION_BP_DIASTOLIC_READINGS);
        }
        if (URION_BP_SYSTOLIC_READINGS != null &&  URION_BP_PULSE_READINGS != null) {
            systolicReadingTextView.setText("Systolic reading: " + URION_BP_SYSTOLIC_READINGS);
            pulseReadingTextView.setText("Pulse reading: " + URION_BP_PULSE_READINGS);
            deviceInfoTextView.setVisibility(View.GONE);
        }
    }


    private void idAssignHere() {
        systolicReadingTextView = findViewById(R.id.systolicReading);
        diastolicReadingTextView = findViewById(R.id.diastolicReading);
        pulseReadingTextView = findViewById(R.id.pulseReading);
        backButton = findViewById(R.id.deviceInfoBackButton);
        deviceNameTextView = findViewById(R.id.deviceNameTextView);
        deviceInfoTextView = findViewById(R.id.deviceInfoTextView);
        linearLayoutDeviceInfo = findViewById(R.id.linearLayoutDeviceInfo);
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

    private void startAutoRefresh() {
        handler.postDelayed(runnable, 1000);
    }

    private void stopAutoRefresh() {
        handler.removeCallbacks(runnable);
    }


}


