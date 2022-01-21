package ru.fazziclay.schoolguide.app.scheduleinformator;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.Settings;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.CompressedEvent;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Schedule;
import ru.fazziclay.schoolguide.util.DataUtil;
import ru.fazziclay.schoolguide.util.time.ConvertMode;
import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class ScheduleInformatorApp {
    public static final String NOTIFICATION_CHANNEL_ID_NONE = "scheduleinformator_none";
    public static final String NOTIFICATION_CHANNEL_ID_NEXT = "scheduleinformator_next";
    public static final String NOTIFICATION_CHANNEL_ID_NOW = "scheduleinformator_now";
    public static final int NOTIFICATION_ID = 1000;

    public final Notification noneNotification;

    private final SchoolGuideApp app;
    private final Context context;
    private final Settings settings;

    private final NotificationManagerCompat notificationManagerCompat;

    private final File scheduleFile;
    private final AppSchedule schedule;

    private UUID currentSchedulePreset = null;

    private InformatorService informatorService = null;
    boolean isServiceForeground = false;

    public ScheduleInformatorApp(SchoolGuideApp app) {
        this.app = app;
        this.context = app.getAndroidContext();
        this.settings = app.getSettings();

        this.notificationManagerCompat = NotificationManagerCompat.from(context);

        this.noneNotification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_NONE)
                .setSmallIcon(R.drawable.planner_s)
                .setAutoCancel(true)
                .setContentTitle("Откисай!")
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)
                .setSound(null)
                .build();

        scheduleFile = new File(app.getFilesDir(), "scheduleinformator.schedule.json");
        schedule = (AppSchedule) DataUtil.load(scheduleFile, AppSchedule.class);
        saveAppSchedule();

        setCurrentPreset(schedule.currentPresetUUID);

        serviceStart();
    }

    public void saveAppSchedule() {
        DataUtil.save(scheduleFile, schedule);
    }

    public void serviceStop() {
       if (informatorService != null) informatorService.stopSelf();
    }

    public void serviceStart() {
        context.startService(new Intent(context, InformatorService.class));
    }

    public Preset getCurrentPreset() {
        Preset p = schedule.getPreset(currentSchedulePreset);
        if (p == null) {
            if (schedule.getPresetsUUIDs().length > 0) {
                currentSchedulePreset = schedule.getPresetsUUIDs()[0];
                return getCurrentPreset();
            }
            p = new Preset();
            p.setName("(PRESET_NULL)"); // TODO: 2022-01-20 fix?
        }
        return p;
    }

    public void setCurrentPreset(UUID preset) {
        currentSchedulePreset = preset;
        schedule.currentPresetUUID = preset;
        saveAppSchedule();
    }

    public int tick() {
        CompressedEvent nowEvent = getCurrentPreset().getNowCompressedEvent();
        CompressedEvent nextEvent = getCurrentPreset().getNextCompressedEvent();
        boolean isNow = nowEvent != null;
        boolean isNext = nextEvent != null;

        if (!isNow && !isNext) {
            if (settings.stopServiceIsNone) {
                stopForeground();
            } else {
                sendNotify(NOTIFICATION_ID, noneNotification);
            }
            return 3000;
        }

        if (!isNow && nextEvent.remainsUntilStart() > settings.scheduleNotifyBeforeTime) {
            if (settings.stopServiceIsNone) {
                stopForeground();
            } else {
                sendNotify(NOTIFICATION_ID, noneNotification);
            }
            return 2000;
        }

        startForeground();

        ScheduleInformatorNotification notification = new ScheduleInformatorNotification();
        notification.smallIcon = R.drawable.planner_s;

        if (isNow) {
            notification.contentTitle = String.format("%s! (%s)", nowEvent.getName(), TimeUtil.convertToHumanTime(nowEvent.remainsUntilEnd(), ConvertMode.hhMMSS));
            if (isNext)
                notification.contentText = String.format("Следующее: %s", nextEvent.getName());

        } else {
            notification.contentTitle = String.format("Откисай! (%s)", TimeUtil.convertToHumanTime(nextEvent.remainsUntilStart(), ConvertMode.hhMMSS));
            notification.contentText = String.format("Следующее: %s", nextEvent.getName());
        }

        sendNotify(NOTIFICATION_ID, notification.toNotification(context, (isNow ? NOTIFICATION_CHANNEL_ID_NOW : NOTIFICATION_CHANNEL_ID_NEXT)));
        return 1000;
    }

    public void onServiceDestroy() {

    }

    public void registerService(InformatorService informatorService) {
        this.informatorService = informatorService;
        startForeground();
    }

    public void startForeground() {
        if (!isServiceForeground) informatorService.startForeground(NOTIFICATION_ID, noneNotification);
        isServiceForeground = true;
    }

    public void stopForeground() {
        informatorService.stopForeground(true);
        isServiceForeground = false;
    }

    public void sendNotify(int id, Notification notification) {
        notificationManagerCompat.notify(id, notification);
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
                    .setOnlyAlertOnce(true)
                    .build();
        }
    }

    public File getScheduleFile() {
        return scheduleFile;
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
