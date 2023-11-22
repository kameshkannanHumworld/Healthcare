package com.example.healthcare;

import static com.example.healthcare.AddMedicationActivity.MEDICTION_ID;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkManager;

import com.example.healthcare.BottomSheetDialog.NotificationBottomSheet;
import com.example.healthcare.NotificationsAndAlarm.ReminderManager;
import com.example.healthcare.NotificationsAndAlarm.ReminderWorker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    TextView medicineName, frequencyTV, quantityTV, effectiveDateOnlyTV, effectiveMonthYearTV, lastEffectiveDateOnlyTV, lastEffectiveMonthYearTV, notesTV, clearAlarmViewMedicationActivity;
    private Switch remainderToggleSwitchViewMedicationActivity;
    private List<String> requestCode;
    MaterialButton alarmButton1, alarmButton2, alarmButton3, alarmButton4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medication);

        //Assign Id Here
        idAssignHere();
        requestCode = new ArrayList<>();

        //data from Medication Fragment
        dataFromMedicationFragment();

        //remainder time display
        displayRemindersForMedicine(this, medictionId);

        //Enable /disable Remainder - toggle switch
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
                    setTagForAlarmButtonMethod();
                    Log.d(TAG, "onCheckedChanged: " + frequencyCode);

                    // Create the notification channel
                    ReminderWorker.createNotificationChannel(getApplicationContext());

                    //alarm count based upon Frequency Code
                    if (requestCode != null && !requestCode.isEmpty()) {
                        remainderButtonCountVisiblityMethod(frequencyCode, true);
                    } else {
                        remainderButtonCountVisiblityMethod(frequencyCode, false);
                    }


                } else {
                    clearAlarmViewMedicationActivity.setVisibility(View.GONE);
                    remainderTimeSlotLinearLayout.setVisibility(View.GONE);
                    remainderList.clear();

                    //delete the remainder for this medicine
                    if (requestCode != null && !requestCode.isEmpty()) {
                        for (String code : requestCode) {
                            Log.d("TAGi", code);
                        }
//                        ReminderManager.clearRemindersForMedicine(getApplicationContext(), requestCode);
                        Snackbar.make(buttonView, "Remainder cleared sucessfully..", Snackbar.LENGTH_LONG).show();
                    }

                }

                //date picker listener -  set alarmButton listener
//                        alarmButton1.setOnClickListener(view -> showTimePicker(alarmButton1));
//
//                        alarmButton2.setOnClickListener(view -> showTimePicker(alarmButton2));
//
//                        alarmButton3.setOnClickListener(view -> showTimePicker(alarmButton3));
//
//                        alarmButton4.setOnClickListener(view -> showTimePicker(alarmButton4));

            }
        });

    }

    private void remainderButtonCountVisiblityMethod(String frequencyCode, boolean isExcisting) {
        int visibleButtons = getVisibleButtonsCount(frequencyCode);

        for (int i = 0; i < 4; i++) {
            Button currentButton = getButtonAtIndex(i);
            if (i < visibleButtons) {
                currentButton.setVisibility(View.VISIBLE);
            } else {
                currentButton.setVisibility(View.INVISIBLE);
            }
        }

        if (isExcisting) {
            List<String> buttonTexts = displayRemindersForMedicine(this, medictionId);
            updateUI(buttonTexts, frequencyCode, true);
        }
    }

    private void updateUI(List<String> buttonTexts, String frequencyCode, boolean buttonDisable) {
        int visibleButtons = getVisibleButtonsCount(frequencyCode);

        for (int i = 0; i < visibleButtons; i++) {
            Button currentButton = getButtonAtIndex(i);
            if (buttonTexts.size() > i) {
                currentButton.setVisibility(View.VISIBLE);
                currentButton.setText(buttonTexts.get(i));
                clearAlarmViewMedicationActivity.setVisibility(View.VISIBLE);
                currentButton.setEnabled(false);

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
        clearAlarmViewMedicationActivity = findViewById(R.id.clearAlarmViewMedicationActivity);


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
        Set<String> filteredReminders = new HashSet<>();
        for (String medicineReminder : reminders) {
            Log.e(TAG, "displayRemindersForMedicine: " +medicineReminder);
            if (medicineReminder.contains(filterPart)) {
                filteredReminders.add(medicineReminder);
            }
        }

        // Display reminders with hour and minute
        StringBuilder stringBuilder = new StringBuilder();
        for (String reminderData : filteredReminders) {
            int hour = -1, minute = -1;

            // Deserialize JSON string to extract reminder data
            try {
                JSONObject jsonObject = new JSONObject(reminderData);
                String uniqID = jsonObject.getString("requestCode");
                requestCode.add(uniqID);
                hour = jsonObject.getInt("hour");
                minute = jsonObject.getInt("minute");

                // Display reminder with hour and minute
                stringBuilder.append(hour).append(":").append(minute)
                        .append("\n")
                        .append(",rc : ").append(uniqID)
                        .append("\n");

                // Add the text for the button to the list
//                buttonTexts.add(String.format("%02d", hour) + ":" + String.format("%02d", minute));

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
        if (requestCode != null && !requestCode.isEmpty()) {
            remainderToggleSwitchViewMedicationActivity.setChecked(true);
            remainderTimeSlotLinearLayout.setVisibility(View.VISIBLE);
        } else {
            remainderToggleSwitchViewMedicationActivity.setChecked(false);
            remainderTimeSlotLinearLayout.setVisibility(View.GONE);
        }

        // Update the UI elements
        updateUI(buttonTexts, frequencyCode, false);

        // Display the reminders with hour and minute in the TextView
//        textView.setText("Reminders with Hour and Minute:\n" + stringBuilder.toString());


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