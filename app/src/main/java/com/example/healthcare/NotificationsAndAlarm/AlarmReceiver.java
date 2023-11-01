
package com.example.healthcare.NotificationsAndAlarm;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.healthcare.AlarmSplashScreenActivity;
import com.example.healthcare.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Intent here
        Intent i = new Intent(context, AlarmSplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );


        //get data from NotificationBottomSheet by Intent
        int medicationId = intent.getIntExtra("MEDICATION_ID", -1);
        int alarmNumber = intent.getIntExtra("ALARM_NUMBER", -1);
        String medicationName = intent.getStringExtra("MEDICATION_NAME");
        int requestCode = medicationId * 100 + alarmNumber; // Unique request code


        //pendingIntent for Scheduling
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, i, PendingIntent.FLAG_IMMUTABLE);


        //SharedPreferences for save the pendingIntent
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pending_intent", pendingIntent.toString());
        editor.apply();


        //set Alarm Sound
        Uri alarmSound =RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );
        MediaPlayer mp = MediaPlayer. create (context, alarmSound);
        mp.start();


        //Set Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Healthcare")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Healthcare - Its time to take your medicine")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(medicationName))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);



        //Notification Manager to post notifications
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(requestCode, builder.build());
    }


}
