package com.example.healthcare.BottomSheetDialog;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.healthcare.NotificationsAndAlarm.AlarmReceiver;
import com.example.healthcare.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Objects;

public class NotificationBottomSheet extends BottomSheetDialogFragment {
    ImageView cancelButton;
    Button alarmButton1, alarmButton2, alarmButton3, alarmButton4;
    MaterialTimePicker picker;
    Calendar calendar;
    TextView clearAlarm;
    LinearLayout layoutRemainder, layoutSwitch;

    AlarmManager alarmManager;
    //PendingIntent pendingIntent;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch remainderSwitch;
    String frequencyCode, medicationName;
    int medicationId;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_bottomsheet, container, false);

        //Assign Id here
        idAssigningMethod(view);

        // Initialize sharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        //set tag for alarm Button
        setTagForAlarmButtonMethod();

        //shared prefrence RemainderSwitch
        sharedPreferencesRemainderMethod();

        //Alarm and Notification Method
        calendar = Calendar.getInstance();
        alarmAndNotificationMethod();

        //cancel button
        cancelButton.setOnClickListener(view1 -> dismiss());

        //clear the alarms
        clearAlarmsMethod();


        return view;
    }

    //clear the remainder
    private void clearAlarmsMethod() {
        clearAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove alarms from AlarmManager
                Intent intent = new Intent(requireContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
                if (alarmManager == null) {
                    alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
                }
                alarmManager.cancel(pendingIntent);

                // Clear alarms from shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for (int i = 1; i <= 4; i++) {
                    editor.remove("alarm_hour_" + medicationId + "_" + i);
                    editor.remove("alarm_minute_" + medicationId + "_" + i);
                    switch (i) {
                        case 1:
                            alarmButton1.setText("Add");
                            break;
                        case 2:
                            alarmButton2.setText("Add");
                            break;
                        case 3:
                            alarmButton3.setText("Add");
                            break;
                        case 4:
                            alarmButton4.setText("Add");
                            break;
                    }
                }
                editor.apply();
                Toast.makeText(requireContext(), "Alarms cleared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //unique id for each remainder
    private void setTagForAlarmButtonMethod() {
        alarmButton1.setTag(1);
        alarmButton2.setTag(2);
        alarmButton3.setTag(3);
        alarmButton4.setTag(4);
    }


    //method for remainder
    private void alarmAndNotificationMethod() {

//        // Check if the permission is granted
//        if (!Settings.canDrawOverlays(requireContext())) {
//            // If not, request it
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireContext().getPackageName()));
//            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
//        }

        //create Notification channel
        createNotificaationChannel();

        //alarm count based upon Frequency Code
        if(Objects.equals(frequencyCode, "ODAY")){
            alarmButton1.setVisibility(View.VISIBLE);
            alarmButton2.setVisibility(View.INVISIBLE);
            alarmButton3.setVisibility(View.INVISIBLE);
            alarmButton4.setVisibility(View.INVISIBLE);
        } else if (Objects.equals(frequencyCode, "TDAY")) {
            alarmButton1.setVisibility(View.VISIBLE);
            alarmButton2.setVisibility(View.VISIBLE);
            alarmButton3.setVisibility(View.INVISIBLE);
            alarmButton4.setVisibility(View.INVISIBLE);
        } else if (Objects.equals(frequencyCode, "THDA")) {
            alarmButton1.setVisibility(View.VISIBLE);
            alarmButton2.setVisibility(View.VISIBLE);
            alarmButton3.setVisibility(View.VISIBLE);
            alarmButton4.setVisibility(View.INVISIBLE);
        } else if (Objects.equals(frequencyCode, "FDAY")) {
            alarmButton1.setVisibility(View.VISIBLE);
            alarmButton2.setVisibility(View.VISIBLE);
            alarmButton3.setVisibility(View.VISIBLE);
            alarmButton4.setVisibility(View.VISIBLE);
        }else{
            alarmButton1.setVisibility(View.VISIBLE);
            alarmButton2.setVisibility(View.VISIBLE);
            alarmButton3.setVisibility(View.INVISIBLE);
            alarmButton4.setVisibility(View.INVISIBLE);
        }

        // set alarmButton listener
        alarmButton1.setOnClickListener(view -> showTimePicker(alarmButton1));

        alarmButton2.setOnClickListener(view -> showTimePicker(alarmButton2));

        alarmButton3.setOnClickListener(view -> showTimePicker(alarmButton3));

        alarmButton4.setOnClickListener(view -> showTimePicker(alarmButton4));

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
//            if (Settings.canDrawOverlays(requireContext())) {
//                // Permission granted
//                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
//            } else {
//                // Permission denied
//                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


    // time picker dialog
    private void showTimePicker(Button alarmButton) {
        picker = new MaterialTimePicker.Builder()
                .setTheme(R.style.TIME_PICKER)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();
        picker.show(getChildFragmentManager(), "Healthcare");

        //time picker positive button
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int requestCode = Integer.parseInt(alarmButton.getTag().toString()); // Unique request code

                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (picker.getHour() == 12) {
                    alarmButton.setText(String.format("%02d", picker.getHour()) + " : " + String.format("%02d", picker.getMinute()) + " PM");
                } else if (picker.getHour() > 12) {
                    alarmButton.setText(String.format("%02d", (picker.getHour() - 12)) + " : " + String.format("%02d", picker.getMinute()) + " PM");
                } else {
                    alarmButton.setText(picker.getHour() + " : " + picker.getMinute() + " AM");
                }

                //alarm set Toast message
                alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(requireContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

                //set shared prefrence for alarm time
                int alarmHour = picker.getHour();
                int alarmMinute = picker.getMinute();

                // Save alarm time to shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("alarm_hour_" + medicationId + "_" + requestCode, alarmHour);
                editor.putInt("alarm_minute_" + medicationId + "_" + requestCode, alarmMinute);
                editor.apply();


                if (alarmManager != null) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

                    if (alarmHour == 12) {
                        alarmButton.setText(String.format("%02d", picker.getHour()) + " : " + String.format("%02d", picker.getMinute()) + " PM");
                    } else if (alarmHour > 12) {
                        alarmButton.setText(String.format("%02d", (picker.getHour() - 12)) + " : " + String.format("%02d", picker.getMinute()) + " PM");
                    } else {
                        alarmButton.setText(picker.getHour() + " : " + picker.getMinute() + " AM");
                    }

                    Toast.makeText(requireContext(), "Alarm set Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to set alarm", Toast.LENGTH_SHORT).show();
                }


            }

        });

    }

    //create push notificaiton channel
    private void createNotificaationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Healthcare";
            String description = "Time for Medication..";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Healthcare", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


        }
    }


    //sharedPreferences to save the remainder enable or not / remainder time
    @SuppressLint("UseCompatLoadingForDrawables")
    private void sharedPreferencesRemainderMethod() {

        boolean switchState = sharedPreferences.getBoolean("switch_state", false);
        remainderSwitch.setChecked(switchState);

        //get data from ViewMedication.class
        Bundle arguments = getArguments();
        if (arguments != null) {
            frequencyCode = arguments.getString("FREQ_CODE_REMAINDER");
            medicationName = arguments.getString("MEDICATION_NAME_REMAINDER");
            String medicationIdString = arguments.getString("MEDI_ID_REMAINDER");
            if (medicationIdString != null) {
                medicationId = Integer.parseInt(medicationIdString);
            }
        }


        //check if switch is on or not
        if (!remainderSwitch.isChecked()) {
            layoutRemainder.setVisibility(View.GONE);

        }

        // Loop through buttons to set alarm times
        for (int i = 1; i <= 4; i++) {

            int alarmHour = sharedPreferences.getInt("alarm_hour_" + medicationId + "_" + i, -1);
            int alarmMinute = sharedPreferences.getInt("alarm_minute_" + medicationId + "_" + i, -1);


            if (alarmHour != -1 && alarmMinute != -1) {
//                String formattedTime = String.format("%02d : %02d", alarmHour, alarmMinute);
                String formattedTime;

                if (alarmHour == 12) {
                    formattedTime = String.format("%02d", alarmHour) + " : " + String.format("%02d", alarmMinute) + " PM";
                } else if (alarmHour > 12) {
                    formattedTime = String.format("%02d", (alarmHour - 12)) + " : " + String.format("%02d", alarmMinute) + " PM";
                } else {
                    formattedTime = String.format("%02d", alarmHour) + " : " + String.format("%02d", alarmMinute) + " AM";
                }


                switch (i) {
                    case 1:
                        alarmButton1.setText(formattedTime);
                        break;
                    case 2:
                        alarmButton2.setText(formattedTime);
                        break;
                    case 3:
                        alarmButton3.setText(formattedTime);
                        break;
                    case 4:
                        alarmButton4.setText(formattedTime);
                        break;
                }
            }
        }

        // Set Switch change listener
        remainderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Save Switch state
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("switch_state", isChecked);
                editor.apply();

                //check if switch is on or not
                if (isChecked) {
                    layoutRemainder.setVisibility(View.VISIBLE);
                    enableAlarms();
                } else {
                    layoutRemainder.setVisibility(View.GONE);
                    disableAlarms();
                }
            }
        });


    }

    //Assign ID for the UI here
    private void idAssigningMethod(View view) {
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        cancelButton = view.findViewById(R.id.cancelButton);
        alarmButton1 = view.findViewById(R.id.alarmButton1);
        alarmButton2 = view.findViewById(R.id.alarmButton2);
        alarmButton3 = view.findViewById(R.id.alarmButton3);
        alarmButton4 = view.findViewById(R.id.alarmButton4);
        remainderSwitch = view.findViewById(R.id.remainderSwitch);
        layoutRemainder = view.findViewById(R.id.layoutRemainder);
        layoutSwitch = view.findViewById(R.id.layoutSwitch);
        clearAlarm = view.findViewById(R.id.clearAlarm);

    }


    //method to enable the remainder
    private void enableAlarms() {
        for (int i = 1; i <= 4; i++) {
            int alarmHour = sharedPreferences.getInt("alarm_hour_" + medicationId + "_" + i, -1);
            int alarmMinute = sharedPreferences.getInt("alarm_minute_" + medicationId + "_" + i, -1);

            if (alarmHour != -1 && alarmMinute != -1) {
                setAlarm(medicationId, i, alarmHour, alarmMinute);            }
        }
    }


    //method to disable the remainder
    private void disableAlarms() {
        for (int i = 1; i <= 4; i++) {
            cancelAlarm(medicationId * 10 + i);
        }
    }

    //method to cancel the remainder
    private void cancelAlarm(int requestCode) {
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
    }

    //send data to set  remainder to AlarmReceiver.class
    private void setAlarm(int medicationId, int alarmNumber, int hour, int minute) {
        int requestCode = medicationId * 100 + alarmNumber;
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("MEDICATION_ID", medicationId);
        intent.putExtra("ALARM_NUMBER", alarmNumber);
        intent.putExtra("MEDICATION_NAME", medicationName);
        Toast.makeText(requireContext(), medicationName, Toast.LENGTH_SHORT).show();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}

