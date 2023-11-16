package com.example.healthcare;

import static com.example.healthcare.AddMedicationActivity.MEDICTION_ID;
import static com.example.healthcare.Fragments.MedicationsFragment.RECYCLER_POSITION_MEDICATION;

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
    int position;
    ImageView medicineImageViewMedicationActivity;
    TextView medicineName, frequencyTV, quantityTV, DateTimeTV, notesTV;


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

        //set date
        if(recordDateTime != null && endDateTime != null){
            DateTimeTV.setText(recordDateTime+" - "+endDateTime);
        }else if (recordDateTime != null){
            DateTimeTV.setText(recordDateTime+" - "+" Not provided");
        } else if (endDateTime != null) {
            DateTimeTV.setText("Not provided"+" - "+endDateTime);
        }else{
            DateTimeTV.setText("Not provided"+" - "+" Not provided");
        }


        notesTV.setText(notes);
        if (quantity == 0) {
            quantityTV.setText(null);
        } else {
            quantityTV.setText(String.valueOf(quantity));
        }

        //set image
        String drawableName = "medicine" + (RECYCLER_POSITION_MEDICATION+1);
        int svgResource = getResources().getIdentifier(drawableName, "drawable", getPackageName());
        medicineImageViewMedicationActivity.setImageResource(svgResource);

    }

    private void idAssignHere() {
        floatingActionButton = findViewById(R.id.fabRemainder);
        backButton = findViewById(R.id.backButton);
        editButton = findViewById(R.id.editButton);
        medicineName = findViewById(R.id.medicineNameViewMedicationsActivity);
        frequencyTV = findViewById(R.id.frequencyViewMedicationsActivity);
        quantityTV = findViewById(R.id.quantityViewMedicationsActivity);
        DateTimeTV = findViewById(R.id.DateTimeViewMedicationsActivity);
        notesTV = findViewById(R.id.notesViewMedicationsActivity);
        medicineImageViewMedicationActivity = findViewById(R.id.medicineImageViewMedicationActivity);

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