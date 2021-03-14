package com.example.registerloginsp;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class App extends Application {
    public static final String CHANNEL_ID="encrptionsServices";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationsChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationsChannel() {
        NotificationChannel encryptionsservice=new NotificationChannel(CHANNEL_ID,
                "Encryptions Is Going On",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager=getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(encryptionsservice);
    }
}
