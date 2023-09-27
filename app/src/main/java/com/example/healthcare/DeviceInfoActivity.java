package com.example.healthcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class DeviceInfoActivity extends AppCompatActivity {
    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        //Assign Id Here
        idAssignHere();

        //back Button Method
        backButtonMethod();
    }
    private void idAssignHere() {
        backButton = findViewById(R.id.deviceInfoBackButton);
    }

    private void backButtonMethod() {
        backButton.setOnClickListener(v -> {
            onBackPressed(); // This will simulate the behavior of the back button press
        });
    }
}