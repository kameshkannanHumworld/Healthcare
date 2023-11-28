package com.example.healthcare.NotificationsAndAlarm;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.startForegroundService;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.healthcare.R;

public class ReminderWorker extends Worker {

    Context context;
    public static final String CHANNEL_ID = "Healthcare";
    public static final String NOTIFICATION = "com.example.healthcare.NOTIFICATION";
    public static final String NOTIFICATION_ID = "com.example.healthcare.NOTIFICATION_ID";



    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        // Your reminder logic goes here
        showReminderNotification();


        return Result.success();
    }

    @SuppressLint("MissingPermission")
    private void showReminderNotification() {

        //set Alarm Sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(context, alarmSound);
        mp.start();


        //Build Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Healthcare")
                .setSmallIcon(R.drawable.humhealthlogoappicon)
                .setContentTitle("Healthcare")
                .setContentText("It's time for your Medicine!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        //notify
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, builder.build());

    }


    //create notification channel
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Healthcare Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Set the notification sound
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .build();
            channel.setSound(alarmSound, audioAttributes);

            // Enable vibration if needed
            channel.enableVibration(true);

            // Set other channel properties as needed
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //delete notification channel
    public static void deleteNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.deleteNotificationChannel(CHANNEL_ID);
        }
    }


}
