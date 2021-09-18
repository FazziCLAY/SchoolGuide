package ru.fazziclay.schoolguide.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

    public static final long[] VIBRATION_NOTIFY_LESSON_START = {0, 200, 100, 500, 100, 200};
    public static final long[] VIBRATION_NOTIFY_LESSON_END = {0, 150, 100, 150, 100, 150, 100};
    public static final long[] VIBRATION_NOTIFY_REST_ENDING = {0, 150, 150, 120, 60, 120, 60, 120};

    public static boolean DEBUG_NOTIFY = false;

    Vibrator vibrator;
    Handler loopHandler;
    Runnable loopRunnable;

    boolean isNotifiedLessonStart = false;
    boolean isNotifiedLessonEnd = false;
    boolean isNotifiedRestEnding = false;

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
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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
        // TODO: 9/18/21 fix bug nullPointer
        SchoolDay currentDay = SchoolWeek.getSchoolWeek().getCurrentDay();
        String subText = "";

        if (DEBUG_NOTIFY) {
            startService(new Intent(this, MainNotificationService.class));
            subText = "STATE:" + currentDay.getState() +
                    "; UL:" + Clock.millisToString(currentDay.getLeftUntilLesson()) +
                    "; UR:" + Clock.millisToString(currentDay.getLeftUntilRest());
        }

        if (currentDay.getState() == SchoolDayState.SCHOOL_END && !DEBUG_NOTIFY) {
            stopService(new Intent(this, MainNotificationService.class));
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.cancel(MainNotificationService.NOTIFICATION_ID);

        } else if (currentDay.getState() == SchoolDayState.SCHOOL_REST) {
            boolean isRestEnding = (currentDay.getLeftUntilLesson() < 3*60*1000);
            if (!isNotifiedLessonEnd) {
                vibrator.vibrate(VIBRATION_NOTIFY_LESSON_END,-1);
                isNotifiedLessonEnd = true;
                isNotifiedLessonStart = false;
                isNotifiedRestEnding = false;
            }
            if (!isNotifiedRestEnding && isRestEnding) {
                vibrator.vibrate(VIBRATION_NOTIFY_REST_ENDING,-1);
                isNotifiedRestEnding = true;
            }

            String title = getString(R.string.rest) + " " + getString(R.string.left, Clock.millisToString(currentDay.getLeftUntilLesson())) +
                    (isRestEnding ? " " + getString(R.string.hurry_up) : "");
            String content = getString(R.string.next_lesson, currentDay.getNextLesson());
            MainNotificationService.updateNotification(this,
                    title,
                    subText,
                    content
            );
            MainWidget.updateAllWidgets(this, title + "\n" + content);

        } else if (currentDay.getState() == SchoolDayState.SCHOOL_LESSON) {
            boolean isLessonEnding = (currentDay.getLeftUntilRest() < 5 * 60 * 1000);
            if (!isNotifiedLessonStart) {
                vibrator.vibrate(VIBRATION_NOTIFY_LESSON_START,-1);
                isNotifiedLessonStart = true;
                isNotifiedRestEnding = false;
                isNotifiedLessonEnd = false;
            }

            String title   = getString(R.string.now_lesson, currentDay.getNowLesson());
            String content = getString(R.string.left, Clock.millisToString(currentDay.getLeftUntilRest())) +
                    (isLessonEnding ? " " + getString(R.string.next_lesson, currentDay.getNextLesson()) : "");
            MainNotificationService.updateNotification(this,
                    title,
                    subText,
                    content
            );
            MainWidget.updateAllWidgets(this, title + "\n" + content);
        }
    }
}