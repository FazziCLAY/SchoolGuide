package ru.fazziclay.schoolguide.android.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.SharedConstrains;

public class ForegroundService extends Service {
    private static final short LOOP_DELAY = 1000;

    static SchoolGuide app = null;

    Handler loopHandler = null;
    Runnable loopRunnable = null;

    boolean isForeground = false;

    @Override
    public void onCreate() {
        if (!SchoolGuide.isInstanceAvailable()) {
            new SchoolGuide(this);
        }
        app = SchoolGuide.getInstance();

        loopHandler = new Handler(Looper.myLooper());
        loopRunnable = () -> {
            if (app.getSelectedLocalSchedule().getState().isEnded()) {
                if (isForeground) {
                    stopForeground(true);
                    isForeground = false;
                }
            } else {
                if (!isForeground) {
                    startForeground(SharedConstrains.MAIN_NOTIFICATION_ID, getDefaultForegroundNotification(this));
                    isForeground = true;
                }
            }

            if (isForeground) app.notificationTick();
            app.updateManifestTick(false);

            loopHandler.postDelayed(loopRunnable, LOOP_DELAY);
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loopHandler.post(loopRunnable);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Notification getDefaultForegroundNotification(Context context) {
        return new NotificationCompat.Builder(context, SharedConstrains.MAIN_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setSilent(true)
                .setContentTitle(context.getString(R.string.notification_foreground_defaultTitle))
                .setContentText(context.getString(R.string.notification_foreground_defaultText))
                .setAutoCancel(true)
                .build();
    }
}