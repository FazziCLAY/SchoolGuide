package ru.fazziclay.schoolguide.app.scheduleinformator;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.Settings;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.CompressedEvent;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.callback.CallbackImportance;
import ru.fazziclay.schoolguide.callback.Status;
import ru.fazziclay.schoolguide.util.AppTrace;
import ru.fazziclay.schoolguide.util.DataUtil;
import ru.fazziclay.schoolguide.util.time.ConvertMode;
import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class ScheduleInformatorApp {
    public static final String NOTIFICATION_CHANNEL_ID_NONE = "scheduleinformator_none";
    public static final String NOTIFICATION_CHANNEL_ID_NEXT = "scheduleinformator_next";
    public static final String NOTIFICATION_CHANNEL_ID_NOW = "scheduleinformator_now";
    public static final int NOTIFICATION_ID = 1000;

    private Notification notification;

    private final SchoolGuideApp app;
    private final AppTrace appTrace;
    private final Context context;
    private final Settings settings;

    private final NotificationManagerCompat notificationManagerCompat;

    private final File scheduleFile;
    private final AppPresetList schedule;

    private InformatorService informatorService = null;
    private boolean isServiceForeground = false;

    public ScheduleInformatorApp(SchoolGuideApp app) {
        this.app = app;
        this.appTrace = app.getAppTrace();
        this.context = app.getAndroidContext();
        this.settings = app.getSettings();

        this.notificationManagerCompat = NotificationManagerCompat.from(context);

        this.notification = getNoneNotification();

        scheduleFile = new File(app.getFilesDir(), "scheduleinformator.schedule.json");
        schedule = DataUtil.load(scheduleFile, AppPresetList.class);
        saveAppSchedule();

        app.getGlobalUpdateCallbacks().addCallback(CallbackImportance.DEFAULT, (globalKeys, globalVersionManifest, globalBuiltinPresetList) -> {
            if (globalBuiltinPresetList != null && globalBuiltinPresetList.presets != null) {
                boolean change = globalBuiltinPresetList.getPresetsIds().length > 0;
                int i = 0;
                while (i < globalBuiltinPresetList.getPresetsIds().length) {
                    UUID gPresetUUID = globalBuiltinPresetList.getPresetsIds()[i];
                    Preset gPreset = globalBuiltinPresetList.getPreset(gPresetUUID);

                    if (gPreset == null || !settings.isBuiltinPresetList) {
                        schedule.removePreset(gPresetUUID);
                    } else {
                        gPreset.setSyncedByGlobal(true);
                        schedule.putPreset(gPresetUUID, gPreset);
                    }

                    i++;
                }

                saveAppSchedule();
                if (change) app.getPresetListUpdateCallbacks().run((callbackStorage, callback) -> callback.onPresetListUpdate());
            }
            return new Status.Builder().build();
        });

        serviceStart();
    }

    public void saveAppSchedule() {
        if (schedule == null) {
            Log.e("saveAppSchedule", "schedule == null!!!!", new NullPointerException("Exception by fazziclay!"));
        }
        DataUtil.save(scheduleFile, schedule);
    }

    public void serviceStop() {
       if (informatorService != null) informatorService.stopSelf();
    }

    public void serviceStart() {
        context.startService(new Intent(context, InformatorService.class));
    }

    public Preset getSelectedPreset() {
        return schedule.getSelectedPreset();
    }

    public void setSelectedPreset(UUID preset) {
        schedule.setSelectedPreset(preset);
        saveAppSchedule();
    }

    public int tick() {
        CompressedEvent nowEvent = getSelectedPreset().getNowCompressedEvent();
        CompressedEvent nextEvent = getSelectedPreset().getNextCompressedEvent();
        boolean isNow = nowEvent != null;
        boolean isNext = nextEvent != null;

        if (!isNow && !isNext) {
            if (settings.isStopForegroundIsNone) {
                stopForeground();
            } else {
                startForeground();
                this.notification = getNoneNotification();
                sendNotify();
            }
            return 3000;
        }

        if (!isNow && nextEvent.remainsUntilStart() > settings.scheduleNotifyBeforeTime) {
            if (settings.isStopForegroundIsNone) {
                stopForeground();
            } else {
                startForeground();
                this.notification = getNoneNotification();
                sendNotify();
            }
            return 2000;
        }

        startForeground();

        ScheduleInformatorNotification notificationBuilder = new ScheduleInformatorNotification();
        notificationBuilder.smallIcon = R.drawable.planner_s;

        if (isNow) {
            String title = context.getString(R.string.scheduleInformator_notification_now_title);
            String message = context.getString(R.string.scheduleInformator_notification_now_next_text);
            notificationBuilder.contentTitle = String.format(title, nowEvent.getName(), TimeUtil.convertToHumanTime(nowEvent.remainsUntilEnd(), ConvertMode.hhMMSS));
            if (isNext)
                notificationBuilder.contentText = String.format(message, nextEvent.getName());

        } else {
            String title = context.getString(R.string.scheduleInformator_notification_next_title);
            String message = context.getString(R.string.scheduleInformator_notification_next_text);
            notificationBuilder.contentTitle = String.format(title, TimeUtil.convertToHumanTime(nextEvent.remainsUntilStart(), ConvertMode.hhMMSS));
            notificationBuilder.contentText = String.format(message, nextEvent.getName());
        }

        notification = notificationBuilder.toNotification(context, (isNow ? NOTIFICATION_CHANNEL_ID_NOW : NOTIFICATION_CHANNEL_ID_NEXT));
        sendNotify();
        return 1000;
    }

    public void onServiceDestroy() {

    }

    public void registerService(InformatorService informatorService) {
        this.informatorService = informatorService;
        startForeground();
    }

    public void startForeground() {
        if (!isServiceForeground) informatorService.startForeground(NOTIFICATION_ID, notification);
        isServiceForeground = true;
    }

    public void stopForeground() {
        informatorService.stopForeground(true);
        isServiceForeground = false;
    }

    public void sendNotify() {
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    public Notification getNoneNotification() {
        String title = context.getString(R.string.scheduleInformator_notification_none_title);
        String message = context.getString(R.string.scheduleInformator_notification_none_text);
        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_NONE)
                .setSmallIcon(R.drawable.planner_s)
                .setAutoCancel(true)
                .setContentTitle(title.isEmpty() ? null : title)
                .setContentText(message.isEmpty() ? null : message)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)
                .setSound(null)
                .build();
    }

    public static class ScheduleInformatorNotification {
        public int smallIcon;
        public String contentTitle;
        public String contentText;
        public String sub;

        public Notification toNotification(Context context, String channelId) {
            return new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(smallIcon)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSubText(sub)
                    .setSound(null)
                    .setSilent(true)
                    .setOnlyAlertOnce(true)
                    .build();
        }
    }

    public File getScheduleFile() {
        return scheduleFile;
    }

    public AppPresetList getSchedule() {
        return schedule;
    }
}
