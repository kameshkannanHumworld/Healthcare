package com.example.healthcare;

import static com.example.healthcare.AddMedicationActivity.MEDICTION_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.healthcare.BottomSheetDialog.NotificationBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ViewMedicationActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    ImageView backButton, editButton;
    String medictionId;
    String frequencyCode;
    TextView medicineName, frequencyTV, quantityTV, recordDateTimeTV, endDateTimeTV, notesTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medication);

        //Assign Id Here
        idAssignHere();

        //data from Medication Fragment
        dataFromMedicationFragment();

        //back Button Method
        backButtonMethod();

        // Set click listener for the FAB
        floatingActionButtonMethod();


    }



    private void dataFromMedicationFragment() {
        Intent intent = getIntent();

        String medName = intent.getStringExtra("MED_NAME");
        String frequency = intent.getStringExtra("FREQUENCY");
        Integer quantity = intent.getIntExtra("QUANTITY", 0); // Default value is 0 if not found
        String recordDateTime = intent.getStringExtra("RECORD_DATE_TIME");
        String endDateTime = intent.getStringExtra("END_DATE_TIME");
        String notes = intent.getStringExtra("NOTES");
        frequencyCode = intent.getStringExtra("FREQ_CODE");
        medictionId = String.valueOf(MEDICTION_ID);

        medicineName.setText(medName);
        frequencyTV.setText(frequency);
        recordDateTimeTV.setText(recordDateTime);
        endDateTimeTV.setText(endDateTime);
        notesTV.setText(notes);
        if (quantity == 0) {
            quantityTV.setText(null);
        } else {
            quantityTV.setText(String.valueOf(quantity));
        }

    }

    private void idAssignHere() {
        floatingActionButton = findViewById(R.id.fabRemainder);
        backButton = findViewById(R.id.backButton);
        editButton = findViewById(R.id.editButton);
        medicineName = findViewById(R.id.medicineName);
        frequencyTV = findViewById(R.id.frequency);
        quantityTV = findViewById(R.id.quantity);
        recordDateTimeTV = findViewById(R.id.recordDateTime);
        endDateTimeTV = findViewById(R.id.endDateTime);
        notesTV = findViewById(R.id.notes);

    }

    private void floatingActionButtonMethod() {
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationBottomSheet.class);
            intent.putExtra("FREQ_CODE_REMAINDER", frequencyCode);
            intent.putExtra("MEDI_ID_REMAINDER", medictionId);
            intent.putExtra("MEDICATION_NAME_REMAINDER", medicineName.getText().toString());

            // Show the bottom sheet dialog
            NotificationBottomSheet notificationBottomSheet = new NotificationBottomSheet();
            notificationBottomSheet.setArguments(intent.getExtras());
            notificationBottomSheet.show(getSupportFragmentManager(), notificationBottomSheet.getTag());
        });

    }

    private void backButtonMethod() {
        backButton.setOnClickListener(v -> {
            onBackPressed(); //back button
        });
    }


}