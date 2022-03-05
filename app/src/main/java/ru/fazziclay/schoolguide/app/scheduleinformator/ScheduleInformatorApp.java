package ru.fazziclay.schoolguide.app.scheduleinformator;

import android.app.Notification;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.app.MilkLog;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.Settings;
import ru.fazziclay.schoolguide.app.SettingsActivity;
import ru.fazziclay.schoolguide.app.global.GlobalBuiltinPresetList;
import ru.fazziclay.schoolguide.app.global.GlobalCacheKeys;
import ru.fazziclay.schoolguide.app.global.GlobalManager;
import ru.fazziclay.schoolguide.app.global.GlobalLatestVersionManifest;
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
    private final AppWidgetManager appWidgetManager;

    private final File scheduleFile;
    private final AppPresetList appPresetList;

    private InformatorService informatorService = null;
    private boolean isServiceForeground = false;

    public ScheduleInformatorApp(SchoolGuideApp app) {
        this.app = app;
        this.appTrace = app.getAppTrace();
        this.context = app.getAndroidContext();
        this.settings = app.getSettings();

        this.notificationManagerCompat = NotificationManagerCompat.from(context);
        this.appWidgetManager = AppWidgetManager.getInstance(context);

        this.notification = getNoneNotification();

        scheduleFile = new File(app.getFilesDir(), "scheduleinformator.schedule.json");
        appPresetList = DataUtil.load(scheduleFile, AppPresetList.class);
        saveAppSchedule();

        app.getUpdatePresetListBuiltinSignalListenerCallbacks().addCallback(CallbackImportance.DEFAULT, (globalBuiltinPresetList, status) -> {
            if (globalBuiltinPresetList != null) {
                boolean change = globalBuiltinPresetList.getPresetsIds().length > 0;
                int i = 0;
                while (i < globalBuiltinPresetList.getPresetsIds().length) {
                    UUID gPresetUUID = globalBuiltinPresetList.getPresetsIds()[i];
                    Preset gPreset = globalBuiltinPresetList.getPreset(gPresetUUID);

                    if (gPreset == null || !status) {
                        appPresetList.removePreset(gPresetUUID);
                    } else {
                        gPreset.setBuiltin(true);
                        appPresetList.putPreset(gPresetUUID, gPreset);
                    }

                    i++;
                }

                saveAppSchedule();
                if (change) app.getPresetListUpdateCallbacks().run((callbackStorage, callback) -> callback.onSignal());
            }
            return new Status.Builder().build();
        });

        app.getGlobalUpdateCallbacks().addCallback(CallbackImportance.DEFAULT, ((globalKeys, globalVersionManifest, globalBuiltinPresetList) -> {
            app.getUpdatePresetListBuiltinSignalListenerCallbacks().run((callbackStorage, callback) -> callback.onSignal(globalBuiltinPresetList, settings.isBuiltinPresetList()));
            return new Status.Builder()
                    .build();
        }));

        app.getOnUserChangeSettingsCallbacks().addCallback(CallbackImportance.DEFAULT, ((key) -> {
            if (key.equals(SettingsActivity.KEY_ADVANCED_IS_BUILTIN_PRESET_LIST)) {
                GlobalManager.getInCurrentThread(app, false, new GlobalManager.ResponseInterface() {
                    @Override
                    public void failed(Exception exception) {
                        MilkLog.g("ошибка при получении глобала в текущем потоке без интернета", exception);
                    }

                    @Override
                    public void success(GlobalCacheKeys keys, GlobalLatestVersionManifest versionManifest, GlobalBuiltinPresetList builtinSchedule) {
                        app.getUpdatePresetListBuiltinSignalListenerCallbacks().run((callbackStorage, callback) -> callback.onSignal(builtinSchedule, settings.isBuiltinPresetList()));
                    }
                });
            }
            return new Status.Builder()
                    .build();
        }));

        serviceStart();
    }

    public void saveAppSchedule() {
        if (appPresetList == null) {
            appTrace.point("saveAppSchedule: schedule == null!!!!", new NullPointerException("Exception by fazziclay!"));
        }
        DataUtil.save(scheduleFile, appPresetList);
    }

    public void serviceStop() {
       if (informatorService != null) informatorService.stopSelf();
    }

    public void serviceStart() {
        context.startService(new Intent(context, InformatorService.class));
    }

    public Preset getSelectedPreset() {
        return appPresetList.getSelectedPreset();
    }

    public void setSelectedPreset(UUID preset) {
        appPresetList.setSelectedPreset(preset);
        saveAppSchedule();
    }

    public int tick() {
        CompressedEvent nowEvent = getSelectedPreset().getNowCompressedEvent();
        CompressedEvent nextEvent = getSelectedPreset().getNextCompressedEvent();
        boolean isNow = nowEvent != null;
        boolean isNext = nextEvent != null;

        ScheduleInformatorNotification notificationBuilder = new ScheduleInformatorNotification();
        notificationBuilder.smallIcon = R.mipmap.ic_launcher;

        if (isNow) {
            String title = context.getString(R.string.scheduleInformator_notification_now_title);
            String message = context.getString(R.string.scheduleInformator_notification_now_next_text);
            notificationBuilder.contentTitle = String.format(title, nowEvent.getName(), TimeUtil.convertToHumanTime(nowEvent.remainsUntilEnd(), ConvertMode.hhMMSS));
            if (isNext)
                notificationBuilder.contentText = String.format(message, nextEvent.getName());

        } else if (isNext) {
            String title = context.getString(R.string.scheduleInformator_notification_next_title);
            String message = context.getString(R.string.scheduleInformator_notification_next_text);
            notificationBuilder.contentTitle = String.format(title, TimeUtil.convertToHumanTime(nextEvent.remainsUntilStart(), ConvertMode.hhMMSS));
            notificationBuilder.contentText = String.format(message, nextEvent.getName());
        }


        // NOTIFY
        if (settings.isNotification()) {
            if (!isNow && !isNext) {
                _isHideNotifyManipulation();

            } else if (!isNow && nextEvent.remainsUntilStart() > settings.getNotificationStatusBeforeTime() && 0 < settings.getNotificationStatusBeforeTime()) {
                _isHideNotifyManipulation();

            } else {
                notification = notificationBuilder.build(context, (isNow ? NOTIFICATION_CHANNEL_ID_NOW : NOTIFICATION_CHANNEL_ID_NEXT));
                _updateNotification();
            }
        } else {
            _isHideNotifyManipulation();
        }
        // NOTIFY
        
        for (Integer appWidgetId : app.getAppWidgetsList().getWidgetsIds()) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);
            views.setTextViewText(R.id.infoTitle, notificationBuilder.contentTitle);
            views.setTextViewText(R.id.infoText, notificationBuilder.contentText);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        return 1000;
    }

    private void _isHideNotifyManipulation() {
        if (settings.isHideEmptyNotification()) {
            stopForeground();
        } else {
            this.notification = getNoneNotification();
            _updateNotification();
        }
    }

    private void _updateNotification() {
        if (!isServiceForeground) {
            startForeground();
        } else {
            sendNotify();
        }
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
                .setSmallIcon(R.mipmap.ic_launcher)
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

        public Notification build(Context context, String channelId) {
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

    public AppPresetList getAppPresetList() {
        return appPresetList;
    }
}
