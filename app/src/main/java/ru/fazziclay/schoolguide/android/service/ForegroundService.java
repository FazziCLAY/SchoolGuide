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

    public static boolean DEBUG_NOTIFY = false;

    static ForegroundService instance = null;

    SchoolWeek schoolWeek = null;
    Settings settings = null;
    StateCache stateCache = null;
    Vibrator vibrator = null;
    Handler loopHandler = null;
    Runnable loopRunnable = null;

    boolean isNotifiedLessonStart = false;
    boolean isNotifiedLessonEnd = false;
    boolean isNotifiedRestEnding = false;

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
        if (System.currentTimeMillis() - stateCache.cacheCreateTime < /*1 * */60 * 1000) {
            isNotifiedLessonStart = stateCache.isNotifiedLessonStart;
            isNotifiedLessonEnd = stateCache.isNotifiedLessonEnd;
            isNotifiedRestEnding = stateCache.isNotifiedRestEnding;
            stateCache.updateTime();
        }

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

        if (!settings.notification) {
            stopService(new Intent(this, MainNotificationService.class));
        }

        if (currentDay.getState() == SchoolDayState.SCHOOL_END && !DEBUG_NOTIFY) {
            loopSchoolEnd();

        } else if (currentDay.getState() == SchoolDayState.SCHOOL_REST) {
            loopRest(currentDay);

        } else if (currentDay.getState() == SchoolDayState.SCHOOL_LESSON) {
            loopLesson(currentDay);
        }
    }

    public void loopSchoolEnd() {
        stopService(new Intent(this, MainNotificationService.class));
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.cancel(MainNotificationService.NOTIFICATION_ID);
    }

    public void loopLesson(SchoolDay currentDay) {
        boolean isLessonEnding = (currentDay.getLeftUntilRest() < 5 * 60 * 1000);
        if (!isNotifiedLessonStart) {
            vibrate(VIBRATION_NOTIFY_LESSON_START);
            isNotifiedLessonStart = true;
            isNotifiedRestEnding = false;
            isNotifiedLessonEnd = false;
            syncCache();
        }

        String title   = getString(R.string.now_lesson, currentDay.getNowLesson());
        String content = getString(R.string.left, Clock.millisToString(currentDay.getLeftUntilRest())) +
                (isLessonEnding ? " " + getString(R.string.next_lesson, currentDay.getNextLesson()) : "");
        if (settings.notification) {
            startService(new Intent(this, MainNotificationService.class));
            MainNotificationService.updateNotification(this,
                    title,
                    null,
                    content
            );
        }
        MainWidget.updateAllWidgets(this, title + "\n" + content);
    }

    public void loopRest(SchoolDay currentDay) {
        boolean isRestEnding = (currentDay.getLeftUntilLesson() < 3*60*1000);
        if (!isNotifiedLessonEnd) {
            vibrate(VIBRATION_NOTIFY_LESSON_END);
            isNotifiedLessonEnd = true;
            isNotifiedLessonStart = false;
            isNotifiedRestEnding = false;
            syncCache();
        }
        if (!isNotifiedRestEnding && isRestEnding) {
            vibrate(VIBRATION_NOTIFY_REST_ENDING);
            isNotifiedRestEnding = true;
            syncCache();
        }

        String title = getString(R.string.rest) + " " + getString(R.string.left, Clock.millisToString(currentDay.getLeftUntilLesson())) +
                (isRestEnding ? " " + getString(R.string.hurry_up) : "");
        String content = getString(R.string.next_lesson, currentDay.getNextLesson());
        if (settings.notification) {
            startService(new Intent(this, MainNotificationService.class));
            MainNotificationService.updateNotification(this,
                    title,
                    null,
                    content
            );
        }
        MainWidget.updateAllWidgets(this, title + "\n" + content);
    }

    public void syncCache() {
        stateCache.updateTime();
        stateCache.isNotifiedLessonStart = isNotifiedLessonStart;
        stateCache.isNotifiedLessonEnd = isNotifiedLessonEnd;
        stateCache.isNotifiedRestEnding = isNotifiedRestEnding;
        StateCache.save(this);
    }

    public void vibrate(long[] tact) {
        if (settings.vibration) vibrator.vibrate(tact,-1);
    }
}