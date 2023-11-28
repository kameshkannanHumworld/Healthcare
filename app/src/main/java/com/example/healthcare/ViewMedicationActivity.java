package com.example.healthcare;

import static com.example.healthcare.AddMedicationActivity.MEDICTION_ID;
import static com.example.healthcare.AddMedicationActivity.PATIENT_ID;
import static com.example.healthcare.AddMedicationActivity.remainderList;
import static com.example.healthcare.BottomSheetDialog.MyBottomSheetDialogFragment.TAG;
import static com.example.healthcare.Converters.ConverterClass.dateFormatSpilitterMethod;
import static com.example.healthcare.Fragments.MedicationsFragment.RECYCLER_POSITION_MEDICATION;
import static com.example.healthcare.NotificationsAndAlarm.ReminderManager.PREFERENCE_KEY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcare.BottomSheetDialog.NotificationBottomSheet;
import com.example.healthcare.NotificationsAndAlarm.RemainderData;
import com.example.healthcare.NotificationsAndAlarm.ReminderManager;
import com.example.healthcare.NotificationsAndAlarm.ReminderWorker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
    FloatingActionButton floatingActionButton;
    ImageView backButton, editButton;
    String medictionId;
    String frequencyCode;
    ImageView medicineImageViewMedicationActivity;
    LinearLayout remainderTimeSlotLinearLayout;
    TextView medicineName, frequencyTV, quantityTV, effectiveDateOnlyTV, effectiveMonthYearTV, lastEffectiveDateOnlyTV, lastEffectiveMonthYearTV, notesTV;
    private Switch remainderToggleSwitchViewMedicationActivity;
    private List<String> requestCodeForThisMedication;
    private List<String> filteredRemindersList;
    private Set<String> filteredReminders;
    private MaterialTimePicker picker;
    private Boolean enableAlarmButtonClick = false;
    MaterialButton alarmButton1, alarmButton2, alarmButton3, alarmButton4;


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



    private void enableDisableRemainders() {

        //textWatcher for switch for enable/disable remainder
        remainderToggleSwitchViewMedicationActivity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (remainderToggleSwitchViewMedicationActivity.isChecked()) {
                    remainderTimeSlotLinearLayout.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onCheckedChanged: " + frequencyCode);

                    // Create the notification channel
                    ReminderWorker.createNotificationChannel(getApplicationContext());

                    //alarm count based upon Frequency Code
                    if (requestCodeForThisMedication != null && !requestCodeForThisMedication.isEmpty()) {
                        remainderButtonCountVisiblityMethod(frequencyCode, true);
                        Log.d(TAG, "onCheckedChanged: if " + frequencyCode);
                        Snackbar.make(buttonView, "Remainder Enabled", Snackbar.LENGTH_LONG).show();
                    } else {
                        remainderButtonCountVisiblityMethod(frequencyCode, false);
                        Log.d(TAG, "onCheckedChanged: else  " + frequencyCode);
                    }


                } else {
                    remainderTimeSlotLinearLayout.setVisibility(View.GONE);
                    remainderList.clear();

                    //delete the remainder for this medicine
                    if (requestCodeForThisMedication != null && !requestCodeForThisMedication.isEmpty()) {
                        for (String code : requestCodeForThisMedication) {
                            Log.d("TAGi", "View medication Activity Toggle button : " + code);
                            ReminderManager.clearRemindersForMedicine(getApplicationContext(), code);
                        }
                        Snackbar.make(buttonView, "Remainder Disabled", Snackbar.LENGTH_LONG).show();
                        requestCodeForThisMedication.clear();
                    }

                }

                //date picker listener -  set alarmButton listener
                alarmButton1.setOnClickListener(view -> {
                    showTimePicker(alarmButton1);
                });

                alarmButton2.setOnClickListener(view -> {
                    showTimePicker(alarmButton2);
                });

                alarmButton3.setOnClickListener(view -> {
                    showTimePicker(alarmButton3);
                });

                alarmButton4.setOnClickListener(view -> {
                    showTimePicker(alarmButton4);
                });


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
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        Log.w("TAG", "Invalid index: " + index);
                    }

                    for (RemainderData data : remainderList) {
                        Log.d("Remainder", "Hour: " + data.getHour() + ", Minute: " + data.getMinute() + ", Tag: " + data.getTag());
                        String uniqueRemainderRequestCode = PATIENT_ID + "_" + MEDICTION_ID + "_" + data.getTag();
                        Log.d(TAG, "Alarm uniqueRemainderRequestCode: " + uniqueRemainderRequestCode);
                        ReminderManager.setReminder(getApplicationContext(), uniqueRemainderRequestCode, data.getHour(), data.getMinute());
                    }

                    Snackbar.make(v, "Remainder Set Sucessfully..", Snackbar.LENGTH_LONG).show();
                    remainderList.clear();


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
        if (recordDateTime != null && endDateTime != null) {
            effectiveDateOnlyTV.setText(dateFormatSpilitterMethod(recordDateTime).get(0));
            effectiveMonthYearTV.setText(dateFormatSpilitterMethod(recordDateTime).get(1));
            lastEffectiveDateOnlyTV.setText(dateFormatSpilitterMethod(endDateTime).get(0));
            lastEffectiveMonthYearTV.setText(dateFormatSpilitterMethod(endDateTime).get(1));
        } else if (recordDateTime != null) {
            effectiveDateOnlyTV.setText(dateFormatSpilitterMethod(recordDateTime).get(0));
            effectiveMonthYearTV.setText(dateFormatSpilitterMethod(recordDateTime).get(1));
            lastEffectiveDateOnlyTV.setText("--");
            lastEffectiveMonthYearTV.setText("Not provided");
        } else if (endDateTime != null) {
            effectiveDateOnlyTV.setText("--");
            effectiveMonthYearTV.setText("Not provided");
            lastEffectiveDateOnlyTV.setText(dateFormatSpilitterMethod(endDateTime).get(0));
            lastEffectiveMonthYearTV.setText(dateFormatSpilitterMethod(endDateTime).get(1));
        } else {
            effectiveDateOnlyTV.setText("--");
            effectiveMonthYearTV.setText("Not provided");
            lastEffectiveDateOnlyTV.setText("--");
            lastEffectiveMonthYearTV.setText("Not provided");
        }


        notesTV.setText(notes);
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
        List<String> list = new ArrayList<String>(filteredReminders);
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
                Log.d(TAG, "displayRemindersForMedicine UUID: " + uuid);
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

}