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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.fazziclaylibs.TimeUtil;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.widgets.MainWidget;
import ru.fazziclay.schoolguide.data.cache.StateCache;
import ru.fazziclay.schoolguide.data.schedule.ScheduleData;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.schedule.State;
import ru.fazziclay.schoolguide.data.settings.Settings;

public class ForegroundService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "Foreground";
    public static final String NOTIFICATION_CHANNEL_NAME = "Фоновое использование";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Использование приложение в фоне, нужно что бы Android не закрывал приложение автоматически";
    public static final int NOTIFICATION_ID = 1;
    public static final int LOOP_DELAY = 1000;

    public static final long[] VIBRATION_NOTIFY_LESSON_START = {0, 300, 200, 600, 200, 300};
    public static final long[] VIBRATION_NOTIFY_LESSON_END = {0, 250, 200, 250, 200, 250, 200};
    public static final long[] VIBRATION_NOTIFY_REST_ENDING = {0, 250, 250, 220, 100, 220, 100, 220};

    static ForegroundService instance = null;
    Gson gson = null;
    Vibrator vibrator = null;
    Settings settings = null;
    ScheduleData scheduleData = null;
    ScheduleProvider scheduleProvider = null;
    StateCache stateCache = null;

    Handler loopHandler = null;
    Runnable loopRunnable = null;

    boolean isEarlyFinished;
    boolean isRestEnding;
    boolean isLessonEnding;
    boolean isSchoolEnd;

    @Override
    public void onCreate() {
        instance = this;
        gson = new GsonBuilder().setPrettyPrinting().create();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        settings = gson.fromJson(FileUtil.read(Settings.getSettingsFilePath(this)), Settings.class);
        scheduleData = gson.fromJson(FileUtil.read(ScheduleData.getScheduleFilePath(this), "{}"), ScheduleData.class);
        scheduleProvider = new ScheduleProvider(getScheduleData());
        stateCache = gson.fromJson(FileUtil.read(StateCache.getStateCacheFilePath(this)), StateCache.class);

        if (settings == null) settings = new Settings();
        if (stateCache == null) stateCache = new StateCache();

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

    public Settings getSettings() {
        return settings;
    }

    public ScheduleData getScheduleData() {
        return scheduleData;
    }

    public ScheduleProvider getScheduleProvider() {
        return scheduleProvider;
    }

    public StateCache getStateCache() {
        return stateCache;
    }

    public boolean isEarlyFinished() {
        return isEarlyFinished;
    }

    public void setEarlyFinished(boolean earlyFinished) {
        isEarlyFinished = earlyFinished;
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
        isEarlyFinished = stateCache.earlyFinishedForDay == new GregorianCalendar().get(Calendar.DAY_OF_YEAR);
        isSchoolEnd = getScheduleProvider().getState() == State.SCHOOL_END || isEarlyFinished;
        isRestEnding = (getScheduleProvider().getLeftUntilLesson() < 3 * 60);
        isLessonEnding = (getScheduleProvider().getLeftUntilRest() < 5 * 60);

        if (!isEarlyFinished && stateCache.earlyFinishedForDay != StateCache.EARLY_FINISHED_FOR_DAY_NOT_SET) {
            stateCache.earlyFinishedForDay = StateCache.EARLY_FINISHED_FOR_DAY_NOT_SET;
            syncCache();
        }

        String title = "unknown";
        String subText = null; // возможно пригодится
        String content = "unknown";

        if (getScheduleProvider().getState() == State.SCHOOL_REST) {
            title = String.format("%s - %s %s",
                    getString(R.string.rest), // Title
                    getString(R.string.left, TimeUtil.secondsToDigitalTime(getScheduleProvider().getLeftUntilLesson())), // Left time
                    (isRestEnding ? getString(R.string.hurry_up) : "") // HURRY UP WARNING
            );
            content = getString(R.string.next_lesson, getScheduleProvider().scheduleLessonToString(getScheduleProvider().getNextLesson()));

        } else if (getScheduleProvider().getState() == State.SCHOOL_LESSON) {
            title = getString(R.string.now_lesson, getScheduleProvider().scheduleLessonToString(getScheduleProvider().getNowLesson()));

            content = String.format("%s %s",
                    getString(R.string.left, TimeUtil.secondsToDigitalTime(getScheduleProvider().getLeftUntilRest())), // Left time
                    ((isLessonEnding && getScheduleProvider().getNextLesson() != null) ? getString(R.string.next_lesson, getScheduleProvider().scheduleLessonToString(getScheduleProvider().getNextLesson())) : "") // Next lesson
            );
        }

        checkVibrationNotify(getScheduleProvider().getState());
        updateMainNotification(this, title, subText, content);
        MainWidget.updateAllWidgets(this, title + "\n" + content);
    }

    public void checkVibrationNotify(State type) {
        if (type == State.SCHOOL_LESSON) {
            if (!stateCache.isNotifiedLessonStart) {
                vibrate(VIBRATION_NOTIFY_LESSON_START);
                stateCache.isNotifiedLessonStart = true;
                stateCache.isNotifiedRestEnding = false;
                stateCache.isNotifiedLessonEnd = false;
                syncCache();
            }

        } else if (type == State.SCHOOL_REST) {
            if (!stateCache.isNotifiedLessonEnd) {
                vibrate(VIBRATION_NOTIFY_LESSON_END);
                stateCache.isNotifiedLessonEnd = true;
                stateCache.isNotifiedLessonStart = false;
                stateCache.isNotifiedRestEnding = false;
                syncCache();
            }
            if (!stateCache.isNotifiedRestEnding && isRestEnding) {
                vibrate(VIBRATION_NOTIFY_REST_ENDING);
                stateCache.isNotifiedRestEnding = true;
                syncCache();
            }
        }
    }

    public void syncCache() {
        stateCache.updateTime();
        stateCache.save(StateCache.getStateCacheFilePath(this));
    }

    public void vibrate(long[] tact) {
        if (settings.vibration) vibrator.vibrate(tact, -1);
    }

    public void updateToDefaultNotification() {
        stateCache.foregroundNotificationState = StateCache.FOREGROUND_NOTIFICATION_STATE_DEFAULT;
        syncCache();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, getForegroundNotification());
    }

    public void updateNotification(Context context, String title, String subText, String contentText) {
        stateCache.foregroundNotificationState = StateCache.FOREGROUND_NOTIFICATION_STATE_MAIN_NOTIFY;
        syncCache();

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
        if (!settings.notification) {
            MainNotificationService.stop(context);
        }

        if (settings.useForegroundNotificationForMain) {
            MainNotificationService.stop(context);
            if (isSchoolEnd) {
                if (stateCache.foregroundNotificationState == StateCache.FOREGROUND_NOTIFICATION_STATE_NOT_SET || stateCache.foregroundNotificationState == StateCache.FOREGROUND_NOTIFICATION_STATE_MAIN_NOTIFY) updateToDefaultNotification();
            } else {
                updateNotification(context, title, subText, contentText);
            }

        } else {
            updateToDefaultNotification();
            if (isSchoolEnd) {
                MainNotificationService.stop(context);
            } else {
                MainNotificationService.updateNotification(context, title, subText, contentText);
            }
        }
    }
}