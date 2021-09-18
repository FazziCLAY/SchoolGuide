package ru.fazziclay.schoolguide.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.fazziclay.fazziclaylibs.FileUtils;
import ru.fazziclay.schoolguide.Clock;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolDay;
import ru.fazziclay.schoolguide.SchoolDayState;
import ru.fazziclay.schoolguide.SchoolWeek;
import ru.fazziclay.schoolguide.android.widgets.MainWidget;

public class ForegroundService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "Foreground";
    public static final String NOTIFICATION_CHANNEL_NAME = "Фоновое использование";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Использование приложение в фоне, нужно что бы Android не закрывал приложение автоматически";
    public static final int NOTIFICATION_ID = 1;
    public static final int LOOP_DELAY = 1000;

    public static boolean DEBUG_NOTIFY = false;

    Handler loopHandler;
    Runnable loopRunnable;

    @Override
    public void onCreate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        startForeground(NOTIFICATION_ID, getForegroundNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loopHandler = new Handler();
        loopRunnable = new Runnable() {
            @Override
            public void run() {
                loop();
                loopHandler.postDelayed(this, LOOP_DELAY);
            }
        };

        loopHandler.post(loopRunnable);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Notification getForegroundNotification() {
        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setSilent(true)
                .setContentTitle("Фоновое использование")
                .setAutoCancel(true)
                .build();
    }

    public void loop() {
        SchoolDay currentDay = SchoolWeek.getSchoolWeek().getCurrentDay();
        String widgetText = "";
        String subText = "";

        if (DEBUG_NOTIFY) {
            startService(new Intent(this, MainNotificationService.class));
            subText = "STATE:" + currentDay.getState() +
                    "; UL:" + Clock.millisToString(currentDay.getLeftUntilLesson()) +
                    "; UR:" + Clock.millisToString(currentDay.getLeftUntilRest());
        }

        if (currentDay.getState() == SchoolDayState.SCHOOL_END && !DEBUG_NOTIFY) {
            widgetText = "";
            stopService(new Intent(this, MainNotificationService.class));
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.cancel(MainNotificationService.NOTIFICATION_ID);

        } else if (currentDay.getState() == SchoolDayState.SCHOOL_REST) {
            String title = getString(R.string.rest) + " " + getString(R.string.left, Clock.millisToString(currentDay.getLeftUntilLesson()));
            String content = getString(R.string.next_lesson, currentDay.getNextLesson());
            MainNotificationService.updateNotification(this,
                    title,
                    subText,
                    content
            );
            widgetText += title + "\n" + content + "\n";

        } else if (currentDay.getState() == SchoolDayState.SCHOOL_LESSON) {
            String title   = getString(R.string.now_lesson, currentDay.getNowLesson());
            String content = getString(R.string.left, Clock.millisToString(currentDay.getLeftUntilRest())) + ((currentDay.getLeftUntilRest() < 5 * 60 * 1000) ? getString(R.string.next_lesson, currentDay.getNextLesson()) : "");
            MainNotificationService.updateNotification(this,
                    title,
                    subText,
                    content
            );
            widgetText += title + "\n" + content + "\n";
        }

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.main_widget);
        views.setTextViewText(R.id.main_text, widgetText);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        String[] widgetsIds = FileUtils.read(getExternalFilesDir("") + MainWidget.WIDGETS_PATH).split("\n");
        int i = 0;
        while (i < widgetsIds.length) {
            try {
                appWidgetManager.updateAppWidget(Integer.parseInt(widgetsIds[i]), views);
            } catch (Exception ignored) {}
            i++;
        }

    }
}