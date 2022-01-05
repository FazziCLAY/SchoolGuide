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
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.AppSchedule;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.CompressedEvent;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.util.time.ConvertMode;
import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class ScheduleInformatorApp {
    public static final String NOTIFICATION_CHANNEL_ID = "schedule_informator_channel";
    public static final int NOTIFICATION_ID = 1000;
    public static Notification FOREGROUND_NOTIFICATION;

    SchoolGuideApp app;
    AppSchedule appSchedule;
    InformatorService informatorService = null;
    boolean isForeground = false;
    NotificationManagerCompat managerCompat;

    Preset selectedPreset;

    public ScheduleInformatorApp(SchoolGuideApp app) {
        this.app = app;

        appSchedule = AppSchedule.load(new File(app.getFilesFir(), AppSchedule.FILE));
        appSchedule.save();

        updateSelected(appSchedule.getSelectedPreset());
        app.getAndroidContext().startService(new Intent(app.getAndroidContext(), InformatorService.class));
    }

    public void stop() {
       if (informatorService != null) informatorService.stopSelf();
    }

    public int tick() {
        CompressedEvent nowEvent = selectedPreset.getNowCompressedEvent();
        CompressedEvent nextEvent = selectedPreset.getNextCompressedEvent();
        boolean isNow = nowEvent != null;
        boolean isNext = nextEvent != null;

        if (!isNow && !isNext) {
            stopForeground();
            return 3000;
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

        managerCompat.notify(NOTIFICATION_ID, notification.toNotification(informatorService, NOTIFICATION_CHANNEL_ID));

        return 1000;
    }

    public void updateSelected(UUID preset) {
        if (preset != null) selectedPreset = appSchedule.getPreset(preset);
        if (selectedPreset == null) selectedPreset = new Preset();
    }

    public void onServiceDestroy() {

    }

    public void registerService(InformatorService informatorService) {
        this.informatorService = informatorService;
        this.managerCompat = NotificationManagerCompat.from(informatorService);

        FOREGROUND_NOTIFICATION = new NotificationCompat.Builder(informatorService, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.planner_s)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground();
    }

    public void startForeground() {
        if (!isForeground) informatorService.startForeground(NOTIFICATION_ID, FOREGROUND_NOTIFICATION);
        isForeground = true;
    }

    public void stopForeground() {
        informatorService.stopForeground(true);
        isForeground = false;
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
                    .setOnlyAlertOnce(true)
                    .build();
        }
    }
}
