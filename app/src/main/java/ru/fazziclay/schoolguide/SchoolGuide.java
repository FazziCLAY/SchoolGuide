package ru.fazziclay.schoolguide;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.fazziclay.schoolguide.android.activity.UpdateCheckerActivity;
import ru.fazziclay.schoolguide.data.cache.StateCacheProvider;
import ru.fazziclay.schoolguide.data.manifest.ManifestProvider;
import ru.fazziclay.schoolguide.data.manifest.VersionState;
import ru.fazziclay.schoolguide.data.schedule.Lesson;
import ru.fazziclay.schoolguide.data.schedule.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.schedule.State;
import ru.fazziclay.schoolguide.data.settings.NotificationStyle;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;
import ru.fazziclay.schoolguide.util.TimeUtil;

/**
 * @author FazziCLAY
 * Главный класс приложения, в нём вся логика самого приложения, дабы не засорять этим кодом классы связанные с андроидом
 * */
public class SchoolGuide {
    static SchoolGuide instance = null;

    Context androidApplicationContext = null;
    Vibrator vibrator = null;

    SettingsProvider settingsProvider = null;
    ScheduleProvider scheduleProvider = null;
    StateCacheProvider stateCacheProvider = null;
    ManifestProvider manifestProvider = null;

    LocalSchedule selectedLocalSchedule = null;

    // Notification
    NotificationManagerCompat notificationManagerCompat = null;
    NotificationStyle notificationStyle = null;
    NotificationData notificationData = new NotificationData();
    boolean isNotificationVisible = false;


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

        load();
        loadNotificationChannels();
    }

    // Загрузка
    private void load() {
        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        settingsProvider = new SettingsProvider(getApplicationContext());
        scheduleProvider = new ScheduleProvider(getApplicationContext());
        stateCacheProvider = new StateCacheProvider(getApplicationContext());
        manifestProvider = new ManifestProvider(getApplicationContext());

        // Notification
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        updateNotificationStyle();

        updateSelectedLocalSchedule();
    }

    // Создать все необходимые каналы для уведомлений
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

    // Обновить веь кеш. Загрузить всё из настроек в переменные
    public void updateNotificationStyle() {
        notificationStyle = settingsProvider.getNotificationStyle();
        notificationStyle.updateCache();
        notificationData.pendingIntent = notificationStyle.getClickAction().getStartActivityInterface().run(getApplicationContext(), this);
    }

    // Обновить переменную selectedLocalSchedule в соответствии с текущий выбором в настройках
    public void updateSelectedLocalSchedule() {
        this.selectedLocalSchedule = scheduleProvider.getLocalSchedule(getSettingsProvider().getSelectedLocalSchedule());
        if (selectedLocalSchedule == null) selectedLocalSchedule = new LocalSchedule(getString(R.string.abc_unknown));
    }

    // Тик уведомления, вызывается из службы ForegroundService а сам он обновляет уведомление
    public void notificationTick() {
        State state = selectedLocalSchedule.getState();

        if (state.isLesson()) {
            isNotificationVisible = true;
            notificationData.isProgress = true;
            notificationData.progressMax = selectedLocalSchedule.getNowLesson().getDuration();
            notificationData.progress = selectedLocalSchedule.getTimeBeforeStartRest();

            if (!state.isEnding()) {
                notificationData.title = getString(R.string.mainNotification_lesson_title);
                notificationData.content = getString(R.string.mainNotification_lesson_content);
                notificationData.sub = getString(R.string.mainNotification_lesson_sub);

            } else {
                notificationData.title = getString(R.string.mainNotification_lesson_title_ending);
                notificationData.content = getString(R.string.mainNotification_lesson_content_ending);
                notificationData.sub = getString(R.string.mainNotification_lesson_sub_ending);
            }

        } else if (state.isRest() && selectedLocalSchedule.getTimeBeforeStartLesson() <= settingsProvider.getNotifyBeforeTime()) {
            isNotificationVisible = true;
            notificationData.isProgress = false;

            if (!state.isEnding()) {
                notificationData.title = getString(R.string.mainNotification_rest_title);
                notificationData.content = getString(R.string.mainNotification_rest_content);

            } else {
                notificationData.title = getString(R.string.mainNotification_rest_title_ending);
                notificationData.content = getString(R.string.mainNotification_rest_content_ending);
            }

        } else {
            if (isNotificationVisible) {
                cancelNotify(SharedConstrains.MAIN_NOTIFICATION_ID);
                isNotificationVisible = false;
            }
            return;
        }

        setMainNotificationReplacements();
        updateMainNotification();

        vibrationTick(state);
    }

    // Заменить все %переменные% в notificationData на нужные значения
    public void setMainNotificationReplacements() {
        String[][] replacements = {
                {"%NOW_LESSON%", getLessonDisplayName(selectedLocalSchedule.getNowLesson())},
                {"%NEXT_LESSON%", getLessonDisplayName(selectedLocalSchedule.getNextLesson())},
                {"%TIME_BEFORE_START_LESSON%", TimeUtil.secondsToHumanTime(selectedLocalSchedule.getTimeBeforeStartLesson(), false)},
                {"%TIME_BEFORE_START_REST%", TimeUtil.secondsToHumanTime(selectedLocalSchedule.getTimeBeforeStartRest(), false)}
        };

        for (String[] replacement : replacements) {
            String r = replacement[0];
            String v = replacement[1];
            if (notificationData.title != null) notificationData.title = notificationData.title.replace(r, v);
            if (notificationData.content != null) notificationData.content = notificationData.content.replace(r, v);
            if (notificationData.sub != null) notificationData.sub = notificationData.sub.replace(r, v);
        }

        if (notificationData.title != null && notificationData.title.equals("-1")) notificationData.title = null;
        if (notificationData.content != null && notificationData.content.equals("-1")) notificationData.content = null;
        if (notificationData.sub != null && notificationData.sub.equals("-1")) notificationData.sub = null;
    }

    // Получить user-friendly имя урока, если урок пустой или имя пустое возвращяем abc_empty или abc_unknown
    public String getLessonDisplayName(Lesson lesson) {
        if (lesson == null) return getString(R.string.abc_empty);
        LessonInfo lessonInfo = getScheduleProvider().getLessonInfo(lesson.getLessonInfo());
        if (lessonInfo == null) return getString(R.string.abc_unknown);
        return lessonInfo.getName();
    }

    // Отправляет главное уведомление в соответстствии с данными в notificationData
    private void updateMainNotification() {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(notificationData.content == null ? null : notificationData.content);
        for (Lesson lesson : selectedLocalSchedule.getToday()) {
            boolean isNow = lesson.equals(selectedLocalSchedule.getNowLesson());
            String ptrStart = isNow ? "-->\t" : "\t\t";
            String ptrEnd = isNow ? "<--" : "";

            String line = String.format(" %s [%s %s] %s %s",
                    ptrStart,
                    TimeUtil.secondsToHumanTime(lesson.getStart(), true).substring(0, 5),
                    TimeUtil.secondsToHumanTime(Math.min(lesson.getEnd(), 24 * 60 * 60 - 1), true).substring(0, 5),
                    getLessonDisplayName(lesson),
                    ptrEnd);
            inboxStyle.addLine(line);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), SharedConstrains.MAIN_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                // FIX WHILE TEXT IN WHITE BACKGROUND BUG #2
                //.setContentTitle(ColorUtils.colorizeText(notificationData.title, Color.WHITE))
                //.setContentText(ColorUtils.colorizeText(notificationData.content, Color.WHITE))
                //.setSubText(ColorUtils.colorizeText(notificationData.sub, Color.WHITE))
                .setContentTitle(notificationData.title)
                .setContentText(notificationData.content)
                .setSubText(notificationData.sub)
                // FIX WHILE TEXT IN WHITE BACKGROUND BUG #2
                .setStyle(inboxStyle)
                .setSilent(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(notificationData.pendingIntent)
                .setSound(null)
                // FIX WHILE TEXT IN WHITE BACKGROUND BUG #2
                //.setColorized(notificationStyle.isColorized())
                //.setColor(notificationStyle.getCachedColor())
                .setDefaults(Notification.DEFAULT_LIGHTS)
                // FIX WHILE TEXT IN WHITE BACKGROUND BUG #2
                .setAutoCancel(true);

        if (notificationData.isProgress) builder.setProgress(notificationData.progressMax, notificationData.progress, notificationData.isProgressIndeterminate);

        sendNotify(SharedConstrains.MAIN_NOTIFICATION_ID, builder.build());
    }

    // Тик обновления манифеста, нужно для того что бы манифест
    // Время от времени проверялся на новую версию(самого манифеста благо manifest.key)
    public void updateManifestTick(boolean activity) {
        int delay = SharedConstrains.UPDATE_MANIFEST_DELAY;
        if (activity) delay = SharedConstrains.UPDATE_MANIFEST_DELAY_ACTIVITY;

        // Сколько прошлос момента последнего обновления
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

    // При обновлении манифеста, нужно в основном только для показа уведомления о новой версии и синхронизации developer schedule
    private void onManifestUpdate(Exception exception, ManifestProvider manifestProvider) {
        if (exception != null || manifestProvider.isTechnicalWorks()) {
            return;
        }

        VersionState versionState = manifestProvider.getAppVersionState();
        if (versionState == VersionState.OUTDATED) {
            sendUpdateCheckerNotify();
        } else {
            if (settingsProvider.isSyncDeveloperSchedule()) scheduleProvider.setSchedule(manifestProvider.getDeveloperSchedule().copy());
            cancelNotify(SharedConstrains.UPDATE_AVAILABLE_NOTIFICATION_ID);
        }
    }

    // Отправляет уведомление о новой версии
    public void sendUpdateCheckerNotify() {
        Intent intent = new Intent(getApplicationContext(), UpdateCheckerActivity.class);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

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

    // Отправляет нужное notification с айди notificationId
    public void sendNotify(int notificationId, Notification notification) {
        notificationManagerCompat.notify(notificationId, notification);
    }

    // Отменяет нужное уведомление с айди notificationId
    public void cancelNotify(int notificationId) {
        notificationManagerCompat.cancel(notificationId);
    }

    // Обновляет вибрацию и стадию в кеше
    private void vibrationTick(State state) {
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

    // Провибрировать в нужный такт
    public void vibrate(long[] tact) {
        if (getSettingsProvider().isVibration()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(tact, -1));
            } else {
                vibrator.vibrate(tact, -1);
            }
        }
    }

    // Показать диолог который информирует о том что редактировать расписания во время включенной синхронизации нельзя!
    public static void showWarnSyncDeveloperScheduleDialog(Context context) {
        AlertDialog.Builder a = new AlertDialog.Builder(context)
                .setTitle("Синхронизация расписания!")
                .setMessage("Вы не можете редактировать/создовать/удалять расписания, пока синхронизация с расписанием разработчика включена!")
                .setPositiveButton("OK", null);

        a.show();
    }

    // Getters
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
        updateSelectedLocalSchedule();
        return selectedLocalSchedule;
    }
}
