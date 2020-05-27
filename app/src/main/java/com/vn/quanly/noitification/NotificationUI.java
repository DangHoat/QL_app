package com.vn.quanly.noitification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationUI extends Application {
    public static final String CHANNEL_1 = "congnotheongay";
    public static final String CHANNEL_2 = "congnotheotien";
    @Override
    public void onCreate() {
        super.onCreate();
        createChanelNoitification();
    }
    public void createChanelNoitification(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificationChannelDateTime = new NotificationChannel(
                    CHANNEL_1,
                    "Channel notification 1", NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannelDateTime.setDescription("Channel notification 1");
            NotificationChannel notificationChannelCost = new NotificationChannel(
                    CHANNEL_1,
                    "Channel notification 1", NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannelCost.setDescription("Channel notification 2");

            NotificationManager notificationManager =getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannelDateTime);
            notificationManager.createNotificationChannel(notificationChannelCost);
        }
    }
}
