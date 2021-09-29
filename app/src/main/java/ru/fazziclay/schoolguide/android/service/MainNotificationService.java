package ru.fazziclay.schoolguide.android.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.fazziclay.schoolguide.R;

public class MainNotificationService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "Main";
    public static final String NOTIFICATION_CHANNEL_NAME = "Главное уведомление";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Суть всего приложения";
    public static final int NOTIFICATION_ID = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        startForeground(NOTIFICATION_ID, new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setAutoCancel(true)
                .build()
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void updateNotification(Context context, String title, String subText, String contentText) {
        context.startService(new Intent(context, MainNotificationService.class));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setSubText(subText)
                .setContentText(contentText)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(NOTIFICATION_ID, builder.build());
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, MainNotificationService.class));
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.cancel(MainNotificationService.NOTIFICATION_ID);
    }
}