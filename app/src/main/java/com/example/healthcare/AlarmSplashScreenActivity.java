package com.example.healthcare;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

@SuppressLint("CustomSplashScreen")
public class AlarmSplashScreenActivity extends AppCompatActivity {
    Button alarmCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_splash_screen);
        statusBarColorMethod();

        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Healthcare";
            String description = "Time for Medication..";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Healthcare", name, importance);
            channel.setDescription(description);

            //Notification
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        //alarm cancel
        alarmCancelButton = findViewById(R.id.alarmCancelButton);
        alarmCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Shared preference for Alarm
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String pendingIntentString = sharedPreferences.getString("pending_intent", null);

                if (pendingIntentString != null) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_IMMUTABLE);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    if (alarmManager != null) {
                        alarmManager.cancel(pendingIntent);
                    }
                }


                // Also, you may want to cancel the notification
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AlarmSplashScreenActivity.this);
                notificationManager.cancel(123);
                
                //Toast
                Toast.makeText(getApplicationContext(), "Alarm Stoppped", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void statusBarColorMethod() {
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.k_blue));
    }
}
