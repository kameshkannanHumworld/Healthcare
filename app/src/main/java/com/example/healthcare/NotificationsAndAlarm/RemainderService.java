package com.example.healthcare.NotificationsAndAlarm;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.healthcare.R;

public class RemainderService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Hello Reminder service ", Toast.LENGTH_SHORT).show();

        // Retrieve notification and notification ID from the intent
        Notification notification = intent.getParcelableExtra(ReminderWorker.NOTIFICATION);
        int notificationId = intent.getIntExtra(ReminderWorker.NOTIFICATION_ID, 0);

        // Display the notification directly
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        assert notification != null;
        notificationManager.notify(notificationId, notification);
    }



}
