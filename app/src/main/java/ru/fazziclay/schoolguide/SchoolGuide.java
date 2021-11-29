package ru.fazziclay.schoolguide;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.fazziclay.schoolguide.android.activity.UpdateCheckerActivity;
import ru.fazziclay.schoolguide.android.activity.schedule.TodayScheduleActivity;
import ru.fazziclay.schoolguide.data.cache.StateCacheProvider;
import ru.fazziclay.schoolguide.data.manifest.ManifestProvider;
import ru.fazziclay.schoolguide.data.manifest.VersionState;
import ru.fazziclay.schoolguide.data.schedule.Lesson;
import ru.fazziclay.schoolguide.data.schedule.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.schedule.State;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;
import ru.fazziclay.schoolguide.util.TimeUtil;

public class SchoolGuide {
    static SchoolGuide instance = null;

    SettingsProvider settingsProvider = null;
    ScheduleProvider scheduleProvider = null;
    StateCacheProvider stateCacheProvider = null;
    ManifestProvider manifestProvider = null;
    Context androidApplicationContext = null;
    Vibrator vibrator = null;
    NotificationManagerCompat notificationManagerCompat = null;

    LocalSchedule selectedLocalSchedule = null;

    public static boolean isInstanceAvailable() {
        return instance != null;
    }

    public static SchoolGuide getInstance() {
        return instance;
    }

    public static void fixInstance(Context context) {
        if (!isInstanceAvailable()) {
            new SchoolGuide(context);
        }
    }

    public SchoolGuide(Context context) {
        if (isInstanceAvailable()) {
            return;
        }
        instance = this;
        this.androidApplicationContext = context;
        this.vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        this.notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        load();
        loadNotificationChannels();
    }

    private void load() {
        settingsProvider = new SettingsProvider(getApplicationContext());
        scheduleProvider = new ScheduleProvider(getApplicationContext());
        stateCacheProvider = new StateCacheProvider(getApplicationContext());
        manifestProvider = new ManifestProvider(getApplicationContext());

        onSelectedLocalScheduleChanged();
    }

    public void onSelectedLocalScheduleChanged() {
        this.selectedLocalSchedule = scheduleProvider.getLocalSchedule(getSettingsProvider().getSelectedLocalSchedule());
        if (selectedLocalSchedule == null) selectedLocalSchedule = new LocalSchedule(getString(R.string.abc_unknown));
    }

    public void updateManifestTick(boolean activity) {
        int delay = SharedConstrains.UPDATE_MANIFEST_DELAY;
        if (activity) delay = SharedConstrains.UPDATE_MANIFEST_DELAY_ACTIVITY;

        long outside = (System.currentTimeMillis() / 1000) - getStateCacheProvider().getLatestAutoManifestCheck();

        if (outside > delay) {
            getManifestProvider().updateForGlobal((exception, manifestProvider) -> {
                try {
                    onManifestUpdate(exception, manifestProvider);
                } catch (Throwable throwable) {
                    new CrashReport(getApplicationContext(), throwable);
                }
            });
            getStateCacheProvider().setLatestAutoManifestCheck();
        }
    }

    private void onManifestUpdate(Exception exception, ManifestProvider manifestProvider) {
        if (manifestProvider.isTechnicalWorks()) {
            return;
        }

        if (exception != null) {
            return;
        }

        VersionState versionState = manifestProvider.getAppVersionState();
        if (versionState == VersionState.OUTDATED) {
            sendUpdateCheckerNotify();
        } else {
            if (getSettingsProvider().isSyncDeveloperSchedule()) getScheduleProvider().setSchedule(manifestProvider.getDeveloperSchedule().copy());
            cancelUpdateCheckerNotify();
        }
    }

    public void notificationTick() {
        LocalSchedule localSchedule = selectedLocalSchedule;
        State scheduleState = localSchedule.getState();

        String titleText = "";
        String subText = "";
        String contentText = "";
        int max = 0;
        int progress = 0;
        int color = Color.CYAN;

        if (scheduleState.isLesson()) {
            titleText = getString(R.string.notification_lesson_title);
            contentText = getString(R.string.notification_lesson_text);

            max = localSchedule.getNowLesson().getDuration();
            progress = localSchedule.getTimeBeforeStartRest();

            if (scheduleState.isEnding()) {
                titleText = getString(R.string.notification_lesson_title_ending);
                contentText = getString(R.string.notification_lesson_text_ending);
                //max = 0;
            }

            titleText = titleText
                    .replace("%LESSON%", getLessonDisplayName(localSchedule.getNowLesson()))
                    .replace("%NEXT_LESSON%", getLessonDisplayName(localSchedule.getNextLesson()))
                    .replace("%LEFT%", TimeUtil.secondsToHumanTime(localSchedule.getTimeBeforeStartRest(), false));

            contentText = contentText
                    .replace("%LESSON%", getLessonDisplayName(localSchedule.getNowLesson()))
                    .replace("%NEXT_LESSON%", getLessonDisplayName(localSchedule.getNextLesson()))
                    .replace("%LEFT%", TimeUtil.secondsToHumanTime(localSchedule.getTimeBeforeStartRest(), false));

        } else if (scheduleState.isRest()) {
            titleText = getString(R.string.notification_rest_title);
            contentText = getString(R.string.notification_rest_text);

            if (scheduleState.isEnding()) {
                titleText = getString(R.string.notification_rest_title_ending);
                contentText = getString(R.string.notification_rest_text_ending);
            }

            titleText = titleText
                    .replace("%LESSON%", getLessonDisplayName(localSchedule.getNowLesson()))
                    .replace("%NEXT_LESSON%", getLessonDisplayName(localSchedule.getNextLesson()))
                    .replace("%LEFT%", TimeUtil.secondsToHumanTime(localSchedule.getTimeBeforeStartLesson(), false));

            contentText = contentText
                    .replace("%LESSON%", getLessonDisplayName(localSchedule.getNowLesson()))
                    .replace("%NEXT_LESSON%", getLessonDisplayName(localSchedule.getNextLesson()))
                    .replace("%LEFT%", TimeUtil.secondsToHumanTime(localSchedule.getTimeBeforeStartLesson(), false));

        } else {
            notificationManagerCompat.cancel(SharedConstrains.MAIN_NOTIFICATION_ID);
            return;
        }

        updateVibration(scheduleState);
        updateNotification(titleText, subText, contentText, color, max, progress);
    }

    public String getLessonDisplayName(Lesson lesson) {
        if (lesson == null) return getString(R.string.abc_empty);
        LessonInfo a = getScheduleProvider().getLessonInfo(lesson.getLessonInfo());
        if (a == null) return getString(R.string.abc_unknown);
        return a.getName();
    }

    public void sendUpdateCheckerNotify() {
        Intent intent = new Intent(getApplicationContext(), UpdateCheckerActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), SharedConstrains.UPDATE_AVAILABLE_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.notification_updatechecker_newVersion_title))
                .setSubText(getString(R.string.notification_updatechecker_newVersion_subText))
                .setContentText(getString(R.string.notification_updatechecker_newVersion_text))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(null);

        sendNotify(SharedConstrains.UPDATE_AVAILABLE_NOTIFICATION_ID, builder.build());
    }

    private void cancelUpdateCheckerNotify() {
        notificationManagerCompat.cancel(SharedConstrains.UPDATE_AVAILABLE_NOTIFICATION_ID);
    }

    private void updateNotification(String title, String subText, String contentText, int color, int max, int progress) {
        Intent intent = new Intent(getApplicationContext(), TodayScheduleActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.InboxStyle a = new NotificationCompat.InboxStyle();
        a.addLine(contentText.equals(" ") ? null : contentText);
        for (Lesson lesson : getSelectedLocalSchedule().getToday()) {
            boolean aa = lesson.equals(getSelectedLocalSchedule().getNowLesson());
            String ptrStart = aa ? "-->\t" : "\t\t";
            String ptrEnd = aa ? "<--" : "";

            String ss = String.format(" %s [%s %s] %s %s",
                    ptrStart,
                    TimeUtil.secondsToHumanTime(lesson.getStart(), true).substring(0, 5),
                    TimeUtil.secondsToHumanTime(Math.min(lesson.getEnd(), 24 * 60 * 60 - 1), true).substring(0, 5),
                    getLessonDisplayName(lesson),
                    ptrEnd);
            a.addLine(ss);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), SharedConstrains.MAIN_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setSubText(subText)
                .setContentText(contentText)
                .setStyle(a)
                .setSilent(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setSound(null)
                .setColorized(false)
                .setColor(color)
                .setAutoCancel(true);

        if (max > 0) builder.setProgress(max, progress, false);

        sendNotify(SharedConstrains.MAIN_NOTIFICATION_ID, builder.build());
    }

    private void sendNotify(int nId, Notification n) {
        notificationManagerCompat.notify(nId, n);
    }

    private void updateVibration(State state) {
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

    @SuppressLint("deprecated")
    public void vibrate(long[] tact) {
        if (getSettingsProvider().isVibration()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(tact, -1));
            } else {
                vibrator.vibrate(tact, -1);
            }
        }
    }

    private void loadNotificationChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            NotificationChannel main = new NotificationChannel(SharedConstrains.MAIN_NOTIFICATION_CHANNEL_ID, getString(R.string.notificationChannel_main_name), NotificationManager.IMPORTANCE_NONE);
            NotificationChannel updateAvailable = new NotificationChannel(SharedConstrains.UPDATE_AVAILABLE_NOTIFICATION_CHANNEL_ID, getString(R.string.notificationChannel_updatechecker_name), NotificationManager.IMPORTANCE_HIGH);
            main.setDescription(getString(R.string.notificationChannel_main_description));
            updateAvailable.setDescription(getString(R.string.notificationChannel_updatechecker_description));
            notificationManager.createNotificationChannel(main);
            notificationManager.createNotificationChannel(updateAvailable);
        }
    }

    public Context getApplicationContext() {
        return this.androidApplicationContext;
    }

    public String getString(int resId) {
        return getApplicationContext().getString(resId);
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

    public ManifestProvider getManifestProvider() {
        return manifestProvider;
    }

    public LocalSchedule getSelectedLocalSchedule() {
        onSelectedLocalScheduleChanged();
        return selectedLocalSchedule;
    }

    public static void showWarnSyncDeveloperScheduleDialog(Context context) {
        AlertDialog.Builder a = new AlertDialog.Builder(context)
                .setTitle("Синхронизация расписания!")
                .setMessage("Вы не можете редактировать/создовать/удалять расписания, пока синхронизация с расписанием разработчика включена!")
                .setPositiveButton("OK", null);

        a.show();
    }
}
