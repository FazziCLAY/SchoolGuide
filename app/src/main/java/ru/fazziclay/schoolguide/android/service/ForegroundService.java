package ru.fazziclay.schoolguide.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.fazziclay.fazziclaylibs.TimeUtil;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.data.cache.StateCache;
import ru.fazziclay.schoolguide.data.cache.StateCacheProvider;
import ru.fazziclay.schoolguide.data.restore_point.RestorePointProvider;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.schedule.State;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;

public class ForegroundService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "Foreground";
    public static final String NOTIFICATION_CHANNEL_NAME = "Фоновое использование";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Использование приложение в фоне, нужно что бы Android не закрывал приложение автоматически";
    public static final short NOTIFICATION_ID = 1;
    public static final short LOOP_DELAY = 1000;

    public static final long[] VIBRATION_NOTIFY_LESSON = {0, 300, 200, 600, 200, 300};
    public static final long[] VIBRATION_NOTIFY_REST = {0, 250, 200, 250, 200, 250, 200};
    public static final long[] VIBRATION_NOTIFY_REST_ENDING = {0, 250, 250, 220, 100, 220, 100, 220};
    public static final long[] VIBRATION_NOTIFY_END = {0, 400, 300, 400, 300, 400, 300, 400, 300, 400, 300, 400, 300, 400};

    static ForegroundService instance = null;

    SettingsProvider settingsProvider = null;
    ScheduleProvider scheduleProvider = null;
    StateCacheProvider stateCacheProvider = null;
    RestorePointProvider restorePointProvider = null;

    Vibrator vibrator = null;
    Handler loopHandler = null;
    Runnable loopRunnable = null;

    @Override
    public void onCreate() {
        instance = this;

        settingsProvider = new SettingsProvider(this);
        scheduleProvider = new ScheduleProvider(this);
        stateCacheProvider = new StateCacheProvider(this);
        restorePointProvider = new RestorePointProvider(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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
        loopHandler = new Handler(Looper.myLooper());
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

    public static ForegroundService getInstance() {
        return instance;
    }

    // Providers
    public SettingsProvider getSettingsProvider() {
        return settingsProvider;
    }
    public ScheduleProvider getScheduleProvider() {
        return scheduleProvider;
    }
    public StateCacheProvider getStateCacheProvider() {
        return stateCacheProvider;
    }
    public RestorePointProvider getRestorePointProvider() {
        return restorePointProvider;
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
        State state = getScheduleProvider().getState();
        String title = "unknown";
        String subText = null; // Возможно понадобится
        String content = "unknown";

        if (state.isRest()) {
            title = String.format("%s - %s %s",
                    getString(R.string.rest), // Title
                    getString(R.string.left, TimeUtil.secondsToDigitalTime(getScheduleProvider().getLeftUntilLesson())), // Left time
                    (state.isEnding() ? getString(R.string.hurry_up) : "") // HURRY UP WARNING
            );
            content = getString(R.string.next_lesson, getScheduleProvider().getNextLesson().getLessonInfo().getName(), getScheduleProvider().getNextLesson().getTeacherInfo().getName());

        } else if (state.isLesson()) {
            title = getString(R.string.now_lesson, getScheduleProvider().getNowLesson().getLessonInfo().getName(), getScheduleProvider().getNowLesson().getTeacherInfo().getName());

            content = String.format("%s %s",
                    getString(R.string.left, TimeUtil.secondsToDigitalTime(getScheduleProvider().getLeftUntilRest())), // Left time
                    (((state.isEnding()) && getScheduleProvider().getNextLesson() != null) ? getString(R.string.next_lesson, getScheduleProvider().getNextLesson().getLessonInfo().getName(), getScheduleProvider().getNextLesson().getTeacherInfo().getName()) : "") // Next lesson
            );
        }

        checkVibrationNotify(getScheduleProvider().getState());
        updateMainNotification(this, title, subText, content);
    }

    public void checkVibrationNotify(State type) {
        if (getStateCacheProvider().getVibratedFor() != type) {
            if (type == State.LESSON) vibrate(VIBRATION_NOTIFY_LESSON);
            if (type == State.REST) vibrate(VIBRATION_NOTIFY_REST);
            if (type == State.REST_ENDING) vibrate(VIBRATION_NOTIFY_REST_ENDING);
            if (type == State.END) vibrate(VIBRATION_NOTIFY_END);
            getStateCacheProvider().setVibratedFor(type);
        }
    }

    public void vibrate(long[] tact) {
        if (getSettingsProvider().isVibration()) vibrator.vibrate(tact, -1);
    }

    public void updateToDefaultNotification() {
        getStateCacheProvider().setForegroundNotificationState(StateCache.FOREGROUND_NOTIFICATION_STATE_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, getForegroundNotification());
    }

    public void updateNotification(Context context, String title, String subText, String contentText) {
        getStateCacheProvider().setForegroundNotificationState(StateCache.FOREGROUND_NOTIFICATION_STATE_MAIN_NOTIFY);

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

    public void updateMainNotification(Context context, String title, String subText, String contentText) {
        if (!getSettingsProvider().isNotification()) {
            MainNotificationService.stop(context);
        }


        boolean isSchoolEnded = getScheduleProvider().getState() == State.END || getStateCacheProvider().isEarlyFinishedForToday();
        if (getSettingsProvider().isUseForegroundNotificationForMain()) {
            MainNotificationService.stop(context);
            if (isSchoolEnded) {
                if (getStateCacheProvider().isForegroundNotificationStateNotDefault()) updateToDefaultNotification();
            } else {
                updateNotification(context, title, subText, contentText);
            }

        } else {
            if (getStateCacheProvider().isForegroundNotificationStateNotDefault()) updateToDefaultNotification();
            if (isSchoolEnded) {
                MainNotificationService.stop(context);
            } else {
                MainNotificationService.updateNotification(context, title, subText, contentText);
            }
        }
    }
}