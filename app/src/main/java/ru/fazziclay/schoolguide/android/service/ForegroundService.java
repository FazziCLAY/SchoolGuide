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

import com.google.gson.Gson;

import java.util.Calendar;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.Clock;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.widgets.MainWidget;
import ru.fazziclay.schoolguide.data.SchoolDay;
import ru.fazziclay.schoolguide.data.SchoolDayState;
import ru.fazziclay.schoolguide.data.SchoolWeek;
import ru.fazziclay.schoolguide.data.Settings;
import ru.fazziclay.schoolguide.data.StateCache;
import ru.fazziclay.schoolguide.data.jsonparser.JsonParser;

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

    SchoolWeek schoolWeek = null;
    Settings settings = null;
    StateCache stateCache = null;
    Vibrator vibrator = null;
    Handler loopHandler = null;
    Runnable loopRunnable = null;

    boolean isEarlyFinished;
    boolean isRestEnding;
    boolean isLessonEnding;
    boolean isSchoolEnd;

    @Override
    public void onCreate() {
        instance = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        JsonParser jsonParser = new JsonParser();
        schoolWeek = jsonParser.parse(jsonParser.getJsonRoot(this));

        Gson gson = new Gson();
        settings = gson.fromJson(FileUtil.read(Settings.getSettingsFilePath(this)), Settings.class);
        if (settings == null) settings = new Settings();

        stateCache = gson.fromJson(FileUtil.read(StateCache.getStateCacheFilePath(this)), StateCache.class);
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

    public static ForegroundService getInstance() {
        return instance;
    }

    public SchoolWeek getSchoolWeek() {
        return schoolWeek;
    }

    public Settings getSettings() {
        return settings;
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
        SchoolWeek week = SchoolWeek.getSchoolWeek();
        if (week == null) {
            return;
        }
        SchoolDay currentDay = week.getCurrentDay();
        if (currentDay == null) {
            return;
        }

        isEarlyFinished = stateCache.earlyFinishedForDay == Clock.getCurrentCalendar().get(Calendar.DAY_OF_YEAR);
        isSchoolEnd = currentDay.getState() == SchoolDayState.SCHOOL_END || isEarlyFinished;
        isRestEnding = (currentDay.getLeftUntilLesson() < 3 * 60 * 1000);
        isLessonEnding = (currentDay.getLeftUntilRest() < 5 * 60 * 1000);

        if (!isEarlyFinished && stateCache.earlyFinishedForDay != StateCache.EARLY_FINISHED_FOR_DAY_NOT_SET) {
            stateCache.earlyFinishedForDay = StateCache.EARLY_FINISHED_FOR_DAY_NOT_SET;
            syncCache();
        }

        String title = "unknown";
        String subText = null;
        String content = "unknown";

        if (currentDay.getState() == SchoolDayState.SCHOOL_REST) {
            title = getString(R.string.rest) + " " + getString(R.string.left, Clock.millisToString(currentDay.getLeftUntilLesson())) +
                    (isRestEnding ? " " + getString(R.string.hurry_up) : "");
            content = getString(R.string.next_lesson, currentDay.getNextLesson());

        } else if (currentDay.getState() == SchoolDayState.SCHOOL_LESSON) {
            title = getString(R.string.now_lesson, currentDay.getNowLesson());
            content = getString(R.string.left, Clock.millisToString(currentDay.getLeftUntilRest())) +
                    ((isLessonEnding && currentDay.getNextLesson() != null) ? " " + getString(R.string.next_lesson, currentDay.getNextLesson()) : "");
        }

        if (settings.vibration) {
            checkVibrationNotify(currentDay.getState());
        }
        updateMainNotification(this, title, subText, content);
        MainWidget.updateAllWidgets(this, title + "\n" + content);
    }

    public void checkVibrationNotify(SchoolDayState type) {
        if (type == SchoolDayState.SCHOOL_LESSON) {
            if (!stateCache.isNotifiedLessonStart) {
                vibrate(VIBRATION_NOTIFY_LESSON_START);
                stateCache.isNotifiedLessonStart = true;
                stateCache.isNotifiedRestEnding = false;
                stateCache.isNotifiedLessonEnd = false;
                syncCache();
            }

        } else if (type == SchoolDayState.SCHOOL_REST) {
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
        StateCache.save(this);
    }

    public void vibrate(long[] tact) {
        if (settings.vibration) vibrator.vibrate(tact, -1);
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

    public void updateToDefaultNotification() {
        stateCache.foregroundNotificationState = StateCache.FOREGROUND_NOTIFICATION_STATE_DEFAULT;
        syncCache();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, getForegroundNotification());
    }
}