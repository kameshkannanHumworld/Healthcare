package com.example.healthcare;

import static com.example.healthcare.AddMedicationActivity.MEDICTION_ID;
import static com.example.healthcare.CommonClass.*;
import static com.example.healthcare.AddMedicationActivity.remainderList;
import static com.example.healthcare.Converters.ConverterClass.dateFormatSpilitterMethod;
import static com.example.healthcare.Fragments.MedicationsFragment.RECYCLER_POSITION_MEDICATION;
import static com.example.healthcare.MainActivity.TAG;
import static com.example.healthcare.NotificationsAndAlarm.ReminderManager.PREFERENCE_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.healthcare.BottomSheetDialog.NotificationBottomSheet;
import com.example.healthcare.NotificationsAndAlarm.RemainderData;
import com.example.healthcare.NotificationsAndAlarm.ReminderManager;
import com.example.healthcare.NotificationsAndAlarm.ReminderWorker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewMedicationActivity extends AppCompatActivity {

    //UI Views
    FloatingActionButton floatingActionButton;
    ImageView backButton, editButton;
    ImageView medicineImageViewMedicationActivity;
    LinearLayout remainderTimeSlotLinearLayout;
    TextView medicineName, frequencyTV, quantityTV, effectiveDateOnlyTV, effectiveMonthYearTV, lastEffectiveDateOnlyTV, lastEffectiveMonthYearTV, notesTV;
    private SwitchMaterial remainderToggleSwitchViewMedicationActivity;
    private MaterialTimePicker picker;
    MaterialButton alarmButton1, alarmButton2, alarmButton3, alarmButton4;
    private RelativeLayout viewMedicationActivityRoot;

    //Datatypes
    String medictionId, frequencyCode;
    private List<String> requestCodeForThisMedication;
    private Set<String> filteredReminders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medication);

        //Assign Id Here
        idAssignHere();
        requestCodeForThisMedication = new ArrayList<>();

        //data from Medication Fragment
        dataFromMedicationFragment();

        //remainder time display
        displayRemindersForMedicine(this, medictionId);

        //Enable /disable Remainder - toggle switch
        setTagForAlarmButtonMethod();
        enableDisableRemainders();

        //back Button Method
        backButtonMethod();

        // Set click listener for the FAB
        floatingActionButtonMethod();


    }


    //method to enable / disable the remainder
    private void enableDisableRemainders() {

        //textWatcher for switch for enable/disable remainder
        remainderToggleSwitchViewMedicationActivity.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (remainderToggleSwitchViewMedicationActivity.isChecked()) {
                remainderTimeSlotLinearLayout.setVisibility(View.VISIBLE);
                Log.d(TAG, "onCheckedChanged: " + frequencyCode);

                // Create the notification channel
                ReminderWorker.createNotificationChannel(getApplicationContext());

                //alarm count based upon Frequency Code
                if (requestCodeForThisMedication != null && !requestCodeForThisMedication.isEmpty()) {
                    remainderButtonCountVisiblityMethod(frequencyCode, true);
                    Log.d(TAG, "onCheckedChanged: if " + frequencyCode);
                    snackBarMethod("Remainder Enabled", false);
                } else {
                    remainderButtonCountVisiblityMethod(frequencyCode, false);
                    Log.d(TAG, "onCheckedChanged: else  " + frequencyCode);
                }

                //disable the button onclickListener
                updateButtonState(alarmButton1);
                updateButtonState(alarmButton2);
                updateButtonState(alarmButton3);
                updateButtonState(alarmButton4);

                //date picker listener -  set alarmButton listener
                alarmButton1.setOnClickListener(view -> showTimePicker(alarmButton1));
                alarmButton2.setOnClickListener(view -> showTimePicker(alarmButton2));
                alarmButton3.setOnClickListener(view -> showTimePicker(alarmButton3));
                alarmButton4.setOnClickListener(view -> showTimePicker(alarmButton4));


            } else {
                remainderTimeSlotLinearLayout.setVisibility(View.GONE);
                remainderList.clear();
                snackBarMethod("Remainder Disabled", false);

                //disable the button onclickListener
                updateButtonState(alarmButton1);
                updateButtonState(alarmButton2);
                updateButtonState(alarmButton3);
                updateButtonState(alarmButton4);

                //delete the remainder for this medicine
                if (requestCodeForThisMedication != null && !requestCodeForThisMedication.isEmpty()) {
                    for (String code : requestCodeForThisMedication) {
                        Log.d(TAG, "View medication Activity Toggle button : " + code);
                        ReminderManager.clearRemindersForMedicine(getApplicationContext(), code);
                    }
//                    snackBarMethod("Remainder Disabled",false);
                    requestCodeForThisMedication.clear();
                }

            }


        });
    }

    // Function to update the state of the button based on its text
    private void updateButtonState(Button myButton) {
        if (myButton.getText().toString().equals("Add")) {
            myButton.setEnabled(true);
        } else {
            myButton.setEnabled(false);
        }
    }

    //time picker to pick the time for remainder
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void showTimePicker(MaterialButton alarmButton) {
        picker = new MaterialTimePicker.Builder()
                .setTheme(R.style.TIME_PICKER)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();
        picker.show(getSupportFragmentManager(), "Healthcare");

        //positive button listener
        picker.addOnPositiveButtonClickListener(v -> {

            // Check for duplication
            RemainderData newRemainderData = new RemainderData(picker.getHour(), picker.getMinute(), alarmButton.getTag().toString());
            if (!isDuplicateRemainder(newRemainderData)) {

                // Set time in button
                if (picker.getHour() == 12) {
                    alarmButton.setText(String.format("%02d", picker.getHour()) + " : " + String.format("%02d", picker.getMinute()) + " PM");
                } else if (picker.getHour() > 12) {
                    alarmButton.setText(String.format("%02d", (picker.getHour() - 12)) + " : " + String.format("%02d", picker.getMinute()) + " PM");
                } else {
                    alarmButton.setText(picker.getHour() + " : " + picker.getMinute() + " AM");
                }

                //disable the button onclickListener
                updateButtonState(alarmButton);

                // Identify the index based on the alarmButton's unique identifier
                int index = getAlarmButtonIndex(alarmButton.getTag());
                Log.d(TAG, "index for alarm : " + index);

                // Save remainder data for set remainder
                if (index >= 0 && index < remainderList.size()) {
                    // If the index is within the current size of the list
                    remainderList.set(index, newRemainderData);
                } else if (index == remainderList.size()) {
                    // If the index is exactly one position after the last index, add a new element
                    remainderList.add(newRemainderData);
                } else {
                    // Handle invalid index (e.g., index < 0)
                    // You may want to log a warning or throw an exception based on your use case
                    Log.w(TAG, "Invalid index: " + index);
                }
            }

        });
    }

    //get each button unique identification
    private int getAlarmButtonIndex(Object tag) {
        String tagString = String.valueOf(tag);

        switch (tagString) {
            case "alarmButton1":
                return 0;
            case "alarmButton2":
                return 1;
            case "alarmButton3":
                return 2;
            case "alarmButton4":
                return 3;
            default:
                return -1; // Handle unknown tag
        }
    }

    //check if any duplicates found in remainder list
    private boolean isDuplicateRemainder(RemainderData newRemainderData) {
        for (RemainderData existingRemainder : remainderList) {
            if (existingRemainder.equals(newRemainderData)) {
                // Duplicate found, do not add
                // You might want to show a message or handle the duplication case in your UI
                return true;
            }
        }
        return false;
    }


    //method for visible the button based on frequency count
    @SuppressLint("SetTextI18n")
    private void remainderButtonCountVisiblityMethod(String frequencyCode, boolean isExcisting) {
        int visibleButtons = getVisibleButtonsCount(frequencyCode);

        for (int i = 0; i < 4; i++) {
            Button currentButton = getButtonAtIndex(i);
            if (i < visibleButtons) {
                currentButton.setVisibility(View.VISIBLE);
                currentButton.setText("Add");
            } else {
                currentButton.setVisibility(View.INVISIBLE);
            }
        }

        if (isExcisting) {
            List<String> buttonTexts = displayRemindersForMedicine(this, medictionId);
            updateUI(buttonTexts, frequencyCode);
        }

    }

    //Update the remainder slot UI Buttons
    private void updateUI(List<String> buttonTexts, String frequencyCode) {
        int visibleButtons = getVisibleButtonsCount(frequencyCode);

        for (int i = 0; i < visibleButtons; i++) {
            Button currentButton = getButtonAtIndex(i);
            if (buttonTexts.size() > i) {
                currentButton.setVisibility(View.VISIBLE);
                currentButton.setText(buttonTexts.get(i));

            } else {
                currentButton.setVisibility(View.GONE);
            }

        }
    }

    //get remainder button index method
    private Button getButtonAtIndex(int index) {
        switch (index) {
            case 0:
                return alarmButton1;
            case 1:
                return alarmButton2;
            case 2:
                return alarmButton3;
            case 3:
                return alarmButton4;
            default:
                throw new IllegalArgumentException("Invalid button index");
        }
    }

    //get the visible count of remainder button count
    private int getVisibleButtonsCount(String frequencyCode) {
        switch (frequencyCode) {
            case "ODAY":
                return 1;
            case "TDAY":
                return 2;
            case "THDA":
                return 3;
            case "FDAY":
                return 4;
            default:
                return 2;
        }
    }

    //data from the Medication Fragment to display
    @SuppressLint({"SetTextI18n", "DiscouragedApi"})
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
        notesTV.setText(notes);
        frequencyTV.setText(frequency);

        // set date
        if (recordDateTime != null) {
            List<String> recordDateParts = dateFormatSpilitterMethod(recordDateTime);
            effectiveDateOnlyTV.setText(recordDateParts.get(0));
            effectiveMonthYearTV.setText(recordDateParts.get(1));
        } else {
            effectiveDateOnlyTV.setText("--");
            effectiveMonthYearTV.setText("Not provided");
        }

        if (endDateTime != null) {
            List<String> endDateParts = dateFormatSpilitterMethod(endDateTime);
            lastEffectiveDateOnlyTV.setText(endDateParts.get(0));
            lastEffectiveMonthYearTV.setText(endDateParts.get(1));
        } else {
            lastEffectiveDateOnlyTV.setText("--");
            lastEffectiveMonthYearTV.setText("Not provided");
        }


        //set quantity
        if (quantity == 0) {
            quantityTV.setText(null);
        } else {
            quantityTV.setText(String.valueOf(quantity));
        }

        //set image
        String drawableName = "medicine" + (RECYCLER_POSITION_MEDICATION + 1);
        int svgResource = getResources().getIdentifier(drawableName, "drawable", getPackageName());
        medicineImageViewMedicationActivity.setImageResource(svgResource);

    }

    //Assign UI id here
    private void idAssignHere() {
        floatingActionButton = findViewById(R.id.fabRemainder);
        backButton = findViewById(R.id.backButton);
        editButton = findViewById(R.id.editButton);
        medicineName = findViewById(R.id.medicineNameViewMedicationsActivity);
        frequencyTV = findViewById(R.id.frequencyViewMedicationsActivity);
        quantityTV = findViewById(R.id.quantityViewMedicationsActivity);
        notesTV = findViewById(R.id.notesViewMedicationsActivity);
        medicineImageViewMedicationActivity = findViewById(R.id.medicineImageViewMedicationActivity);
        effectiveDateOnlyTV = findViewById(R.id.effectiveDateOnlyTV);
        effectiveMonthYearTV = findViewById(R.id.effectiveMonthYearTV);
        lastEffectiveDateOnlyTV = findViewById(R.id.lastEffectiveDateOnlyTV);
        lastEffectiveMonthYearTV = findViewById(R.id.lastEffectiveMonthYearTV);
        remainderTimeSlotLinearLayout = findViewById(R.id.remainderTimeSlotLinearLayout);
        remainderToggleSwitchViewMedicationActivity = findViewById(R.id.remainderToggleSwitchViewMedicationActivity);
        alarmButton1 = findViewById(R.id.alarmButton1ViewMedicationActivity);
        alarmButton2 = findViewById(R.id.alarmButton2ViewMedicationActivity);
        alarmButton3 = findViewById(R.id.alarmButton3ViewMedicationActivity);
        alarmButton4 = findViewById(R.id.alarmButton4ViewMedicationActivity);
        viewMedicationActivityRoot = findViewById(R.id.viewMedicationActivityRoot);
    }

    //floating action button
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

    //onPuase for set the remainder
    @Override
    protected void onPause() {
        super.onPause();

        //set the remainder
        for (RemainderData data : remainderList) {
            Log.d(TAG, "Hour: " + data.getHour() + ", Minute: " + data.getMinute() + ", Tag: " + data.getTag());
            String uniqueRemainderRequestCode = PATIENT_ID + "_" + MEDICTION_ID + "_" + data.getTag();
            Log.d(TAG, "Alarm uniqueRemainderRequestCode: " + uniqueRemainderRequestCode);
            ReminderManager.setReminder(getApplicationContext(), uniqueRemainderRequestCode, data.getHour(), data.getMinute(), medicineName.getText().toString());
        }
        remainderList.clear();
        snackBarMethod("Remainder Set Sucessfully", false);

    }

    //back button listener
    private void backButtonMethod() {
        backButton.setOnClickListener(v -> {
            onBackPressed(); //back button
        });
    }


    /*  display the remainder we saved in shared preference
     *       params1 - context
     *       params2 - medication ID (for filter remainder)
     *       params3 - TextView for setText */
    @SuppressLint("DefaultLocale")
    private List<String> displayRemindersForMedicine(Context context, String filterPart) {
        List<String> buttonTexts = new ArrayList<>();

        // Retrieve the set of reminders from SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        Set<String> reminders = preferences.getStringSet(PREFERENCE_KEY, new HashSet<>());

        //filter remainder for selected medicine
        filteredReminders = new HashSet<>();
        for (String medicineReminder : reminders) {
            Log.e(TAG, "displayRemindersForMedicine: " + medicineReminder);
            if (medicineReminder.contains(filterPart)) {
                filteredReminders.add(medicineReminder);
            }
        }

        //display and sort filter medicines
        // Sorting HashSet using List
        List<String> list = new ArrayList<>(filteredReminders);
        Collections.sort(list);


        // Display reminders with hour and minute
        for (String reminderData : list) {
            Log.d(TAG, "display Reminders For this Medicine: " + reminderData);
            int hour = -1, minute = -1;
            String uuid;

            // Deserialize JSON string to extract reminder data
            try {
                JSONObject jsonObject = new JSONObject(reminderData);
                String uniqID = jsonObject.getString("requestCode");
                hour = jsonObject.getInt("hour");
                minute = jsonObject.getInt("minute");
                uuid = jsonObject.getString("uuid");
                requestCodeForThisMedication.add(uuid);

                // Add the text for the button to the list
                if (hour == 12) {
                    buttonTexts.add(String.format("%02d", hour) + " : " + String.format("%02d", minute) + " PM");
                } else if (hour > 12) {
                    buttonTexts.add(String.format("%02d", (hour - 12)) + " : " + String.format("%02d", minute) + " PM");
                } else {
                    buttonTexts.add(String.format("%02d", hour) + " : " + String.format("%02d", minute) + " AM");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //below code to On/off the switch, when any alarm is active/inactive
        if (requestCodeForThisMedication != null && !requestCodeForThisMedication.isEmpty()) {
            remainderToggleSwitchViewMedicationActivity.setChecked(true);
            remainderTimeSlotLinearLayout.setVisibility(View.VISIBLE);
        } else {
            remainderToggleSwitchViewMedicationActivity.setChecked(false);
            remainderTimeSlotLinearLayout.setVisibility(View.GONE);
        }

        // Update the UI elements
        updateUI(buttonTexts, frequencyCode);


        // Return the list of button texts
        return buttonTexts;

    }

    //unique Tag for each button
    private void setTagForAlarmButtonMethod() {
        alarmButton1.setTag("alarmButton1");
        alarmButton2.setTag("alarmButton2");
        alarmButton3.setTag("alarmButton3");
        alarmButton4.setTag("alarmButton4");
    }


    //common snack bar for this fragment
    private void snackBarMethod(String message, Boolean isToast) {
        if (!isToast) {
            Snackbar.make(viewMedicationActivityRoot, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    }

}