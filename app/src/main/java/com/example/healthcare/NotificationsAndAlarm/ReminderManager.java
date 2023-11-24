package com.example.healthcare.NotificationsAndAlarm;

import static com.example.healthcare.BottomSheetDialog.MyBottomSheetDialogFragment.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ReminderManager {

    private static String REMINDER_WORK_TAG_PREFIX = "medicine_reminder_";
    public static String PREFERENCE_KEY = "medicine_reminders";


    /*
     *   Set remainder using this method
     *       Params1 - context
     *       params2 - unique remainder request code (String)
     *       params3 - hour (remainder)
     *       params4 - Minute (remainder)    */
    public static void setReminder(Context context, String uniqueRemainderRequestCode, int hour, int minute) {

        // Calculate the delay until the specified time
        long delayInMillis = calculateDelay(hour, minute);

        // Create a OneTimeWorkRequest to trigger the ReminderWorker with the calculated delay
        OneTimeWorkRequest reminderWorkRequest =
                new OneTimeWorkRequest.Builder(ReminderWorker.class)
                        .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                        .addTag(getReminderTag(uniqueRemainderRequestCode))
                        .build();

        // Enqueue the work request
        Log.e(TAG, "setReminder: "+reminderWorkRequest );
        WorkManager.getInstance(context).enqueue(reminderWorkRequest);

        // Save the reminder for the medicine
        saveReminder(context, getReminderTag(uniqueRemainderRequestCode), hour, minute);
    }

    /*
     *   Clear Particular remainder for one Medicine
     *       Params1 - context
     *       params2 - unique Remainder Request Code(String)*/
    public static void clearRemindersForMedicine(Context context, String uniqueRemainderRequestCode) {
        // Cancel all work with the specified tag (in this case, the medicine reminder tag)
        WorkManager.getInstance(context).cancelAllWorkByTag(uniqueRemainderRequestCode);

        // Remove the reminder for the medicine
        removeReminder(context, uniqueRemainderRequestCode);
    }

    //timing logic here
    private static long calculateDelay(int hour, int minute) {
        // Get the current time
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        // Calculate the delay until the specified time
        long delayInMillis = 0;

        if (currentHour < hour || (currentHour == hour && currentMinute < minute)) {
            // The specified time is in the future for today
            Calendar specifiedTime = Calendar.getInstance();
            specifiedTime.set(Calendar.HOUR_OF_DAY, hour);
            specifiedTime.set(Calendar.MINUTE, minute);
            specifiedTime.set(Calendar.SECOND, 0);
            specifiedTime.set(Calendar.MILLISECOND, 0);

            delayInMillis = specifiedTime.getTimeInMillis() - currentTime.getTimeInMillis();
        } else {
            // The specified time has already passed for today, set the reminder for the same time tomorrow
            Calendar nextDayTime = Calendar.getInstance();
            nextDayTime.add(Calendar.DAY_OF_YEAR, 1);
            nextDayTime.set(Calendar.HOUR_OF_DAY, hour);
            nextDayTime.set(Calendar.MINUTE, minute);
            nextDayTime.set(Calendar.SECOND, 0);
            nextDayTime.set(Calendar.MILLISECOND, 0);

            delayInMillis = nextDayTime.getTimeInMillis() - currentTime.getTimeInMillis();
        }

        return delayInMillis;
    }

    //get the unique remainder tag by calling this method
    private static String getReminderTag(String uniqueRemainderRequestCode) {
        // Use a unique tag for each medicine reminder
        return REMINDER_WORK_TAG_PREFIX + uniqueRemainderRequestCode;
    }


    /*
     *   Save the remainder in shared preference
     *       params1 - context
     *       params2 - (String) unique Remainder Request Code*/
    private static void saveReminder(Context context, String uniqueRemainderRequestCode, int hour, int minute) {
        // Save the reminder data (including hour and minute) to SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        Set<String> reminders = preferences.getStringSet(PREFERENCE_KEY, new HashSet<>());

        // Serialize reminder data to JSON
        String reminderData = createReminderData(uniqueRemainderRequestCode, hour, minute);

        //add data to the set
        reminders.add(reminderData);

        for(String s : reminders){
            // Logging for debugging
            Log.d("TAGi", "After adding reminder: " + s);
        }

        clearAllRemindersFromPreferences(context);
        preferences.edit().putStringSet(PREFERENCE_KEY, reminders).apply();

    }


    private static String createReminderData(String uniqueRemainderRequestCode, int hour, int minute) {
        // Create a JSON object to store reminder data
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("requestCode", uniqueRemainderRequestCode);
            jsonObject.put("hour", hour);
            jsonObject.put("minute", minute);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    /*
     *   remove the remainder in shared preference
     *       params1 - context
     *       params2 - (String) unique Remainder Request Code*/
    private static void removeReminder(Context context, String uniqueRemainderRequestCode) {
        // Remove the medicine ID from SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        Set<String> reminders = preferences.getStringSet(PREFERENCE_KEY, new HashSet<>());
        reminders.remove(uniqueRemainderRequestCode);
        preferences.edit().putStringSet(PREFERENCE_KEY, reminders).apply();
    }


    /*
     *   Clear all the remainders for all medicine
     *       params1 - context  */
    public static void clearAllReminders(Context context) {
        // Cancel all work in the WorkManager
        WorkManager.getInstance(context).cancelAllWork();

        // Clear all reminders from SharedPreferences
        clearAllRemindersFromPreferences(context);

        Toast.makeText(context, "All Remainders Cleared Sucessfully..", Toast.LENGTH_SHORT).show();
    }


    /*
     *   Clear all the remainders for all medicine in preference
     *       params1 - context  */
    private static void clearAllRemindersFromPreferences(Context context) {
        // Clear all reminders from SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }




}
