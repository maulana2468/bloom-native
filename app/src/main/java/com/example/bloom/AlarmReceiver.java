package com.example.bloom;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        setNotification(context, intent);
    }

    private void setNotification(Context context, Intent intent) {
        String title = intent.getStringExtra("TITLE_NO");
        String jam = intent.getStringExtra("DATE_NO");

        Notification.Builder notificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(context, "channel02");
        } else {
            notificationBuilder = new Notification.Builder(context);
        }

        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        notificationBuilder.setContentTitle("Bloom - Timer");
        notificationBuilder.setContentText(jam + " - " + title);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setAutoCancel(true);
        //notificationBuilder.setContentIntent(mainPendingIntent);
        notificationBuilder.setStyle(
                new Notification.BigTextStyle().setBigContentTitle("Bloom - Timer")
                        .bigText(jam + " - " + title)
        );
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(new Random().nextInt(), notificationBuilder.build());
    }
}
