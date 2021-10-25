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
import android.text.Spannable;
import android.text.SpannableString;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.data.cache.NotificationState;
import ru.fazziclay.schoolguide.data.cache.StateCacheProvider;
import ru.fazziclay.schoolguide.data.manifest.ManifestProvider;
import ru.fazziclay.schoolguide.data.schedule.Lesson;
import ru.fazziclay.schoolguide.data.schedule.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.schedule.State;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;
import ru.fazziclay.schoolguide.data.settings.UserNotification;
import ru.fazziclay.schoolguide.util.TimeUtil;

public class ForegroundService extends Service {
    private static final short LOOP_DELAY = 1000;

    static ForegroundService instance = null;

    ManifestProvider manifestProvider = null;
    SettingsProvider settingsProvider = null;
    ScheduleProvider scheduleProvider = null;
    StateCacheProvider stateCacheProvider = null;

    Vibrator vibrator = null;
    Handler loopHandler = null;
    Runnable loopRunnable = null;
    NotificationManagerCompat notificationManagerCompat;

    int internetTerminator = 10 * 60;

    @Override
    public void onCreate() {
        instance = this;

        manifestProvider = new ManifestProvider(this);
        settingsProvider = new SettingsProvider(this);
        scheduleProvider = new ScheduleProvider(this);
        stateCacheProvider = new StateCacheProvider(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        loopHandler = new Handler(Looper.myLooper());
        loopRunnable = () -> {
            loop();
            loopHandler.postDelayed(loopRunnable, LOOP_DELAY);
        };
        notificationManagerCompat = NotificationManagerCompat.from(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);

            NotificationChannel foregroundChannel = new NotificationChannel(SharedConstrains.FOREGROUND_NOTIFICATION_CHANNEL_ID, getString(R.string.notificationChannel_foreground_name), NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel externalChannel = new NotificationChannel(SharedConstrains.EXTERNAL_NOTIFICATION_CHANNEL_ID, getString(R.string.notificationChannel_external_name), NotificationManager.IMPORTANCE_HIGH);

            foregroundChannel.setDescription(getString(R.string.notificationChannel_foreground_description));
            externalChannel.setDescription(getString(R.string.notificationChannel_external_description));

            notificationManager.createNotificationChannel(foregroundChannel);
            notificationManager.createNotificationChannel(externalChannel);
        }

        startForeground(SharedConstrains.FOREGROUND_NOTIFICATION_ID, getDefaultForegroundNotification());
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

    public static ForegroundService getInstance() {
        return instance;
    }

    // Providers
    public ManifestProvider getManifestProvider() {
        return manifestProvider;
    }
    public SettingsProvider getSettingsProvider() {
        return settingsProvider;
    }
    public ScheduleProvider getScheduleProvider() {
        return scheduleProvider;
    }
    public StateCacheProvider getStateCacheProvider() {
        return stateCacheProvider;
    }

    public Notification getDefaultForegroundNotification() {
        return new NotificationCompat.Builder(this, SharedConstrains.FOREGROUND_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setSilent(true)
                .setContentTitle(getString(R.string.notification_foreground_defaultTitle))
                .setContentText(getString(R.string.notification_foreground_defaultText))
                .setAutoCancel(true)
                .build();
    }

    public String getLessonText(Lesson lesson) {
        LessonInfo a = getScheduleProvider().getLessonInfo(lesson.getLessonInfo());
        if (a == null) return "Unknown";
        return a.getName();
    }

    public void loop() {
        internetTerminator++;
        if (internetTerminator > 10 * 60) {
            new Thread(() -> getManifestProvider().updateForGlobal()).start();
            internetTerminator = 0;
        }

        ScheduleProvider sp = getScheduleProvider();
        UUID sls = getSettingsProvider().getSelectedLocalSchedule();
        State state = sp.getState(sls);
        Lesson nextLesson = sp.getNextLesson(sls);

        SpannableString title = null;
        SpannableString content;
        SpannableString subText = null;

        String contentS = String.format("Left: %s %s",
                TimeUtil.secondsToHumanTime(sp.getTimeBeforeStartRest(sls), false),
                (((state.isLesson() && state.isEnding()) || state.isRest()) && nextLesson != null ? "Next: "+getLessonText(nextLesson) : "")
        );
        content = new SpannableString(contentS);

        if (state.isLesson()) {
            String titleS = String.format("Now: %s",
                    getLessonText(sp.getNowLesson(sls))
            );

            title = new SpannableString(titleS);

        } else if (state.isRest()) {
            title = new SpannableString("Rest!" + (state.isEnding() ? " (HURRY UP)" : ""));
        }

        updateVibration(state);
        updateUserNotification(title, subText, content);
    }

    public void updateVibration(State state) {
        if (getStateCacheProvider().getVibratedFor() != state) {
            if (state == State.LESSON) vibrate(SharedConstrains.VIBRATION_NOTIFY_LESSON);
            if (state == State.REST) {
                if (!state.isEnding()) vibrate(SharedConstrains.VIBRATION_NOTIFY_REST);
                if (state.isEnding()) vibrate(SharedConstrains.VIBRATION_NOTIFY_REST_ENDING);
            }
            if (state == State.END) vibrate(SharedConstrains.VIBRATION_NOTIFY_END);
            getStateCacheProvider().setVibratedFor(state);
        }
    }

    public void vibrate(long[] tact) {
        if (getSettingsProvider().isVibration()) vibrator.vibrate(tact, -1);
    }

    public void updateUserNotification(Spannable title, Spannable subText, Spannable contentText) {
        boolean isSchoolEnded = getScheduleProvider().getState(settingsProvider.getSelectedLocalSchedule()) == State.END;

        if (getSettingsProvider().isNotification() && !isSchoolEnded) {
            if (getSettingsProvider().getUserNotification() == UserNotification.FOREGROUND) {
                updateNotification(SharedConstrains.FOREGROUND_NOTIFICATION_CHANNEL_ID, SharedConstrains.FOREGROUND_NOTIFICATION_ID, title, subText, contentText);
                if (!getStateCacheProvider().isExternalNotificationStateDefault()) updateExternalNotificationToDefault();
            } else {
                updateNotification(SharedConstrains.EXTERNAL_NOTIFICATION_CHANNEL_ID, SharedConstrains.EXTERNAL_NOTIFICATION_ID, title, subText, contentText);
                if (!getStateCacheProvider().isForegroundNotificationStateDefault()) updateForegroundNotificationToDefault();
            }


        } else {
            if (!getStateCacheProvider().isForegroundNotificationStateDefault()) updateForegroundNotificationToDefault();
            if (!getStateCacheProvider().isExternalNotificationStateDefault()) updateExternalNotificationToDefault();
        }
    }

    private void updateExternalNotificationToDefault() {
        getStateCacheProvider().setExternalNotificationState(NotificationState.DEFAULT);
        notificationManagerCompat.cancel(SharedConstrains.EXTERNAL_NOTIFICATION_ID);
    }

    public void updateForegroundNotificationToDefault() {
        getStateCacheProvider().setForegroundNotificationState(NotificationState.DEFAULT);
        notificationManagerCompat.notify(SharedConstrains.FOREGROUND_NOTIFICATION_ID, getDefaultForegroundNotification());
    }

    // Обновить уведомление главной (этой) службы
    public void updateNotification(String notificationChannelId, int notificationId, Spannable title, Spannable subText, Spannable contentText) {
        if (notificationChannelId.equals(SharedConstrains.FOREGROUND_NOTIFICATION_CHANNEL_ID)) getStateCacheProvider().setForegroundNotificationState(NotificationState.CUSTOM);
        if (notificationChannelId.equals(SharedConstrains.EXTERNAL_NOTIFICATION_CHANNEL_ID)) getStateCacheProvider().setExternalNotificationState(NotificationState.CUSTOM);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setSubText(subText)
                .setContentText(contentText)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setAutoCancel(true);

        notificationManagerCompat.notify(notificationId, builder.build());
    }
}