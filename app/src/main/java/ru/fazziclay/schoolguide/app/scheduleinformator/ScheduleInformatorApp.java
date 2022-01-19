package ru.fazziclay.schoolguide.app.scheduleinformator;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.Settings;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.CompressedEvent;
import ru.fazziclay.schoolguide.util.DataUtil;
import ru.fazziclay.schoolguide.util.time.ConvertMode;
import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class ScheduleInformatorApp {
    public static final String NOTIFICATION_CHANNEL_ID = "schedule_informator_channel";
    public static final int NOTIFICATION_ID = 1000;
    public static Notification FOREGROUND_NOTIFICATION;

    SchoolGuideApp app;

    File appScheduleFile;

    AppSchedule appSchedule;
    InformatorService informatorService = null;
    boolean isForeground = false;
    NotificationManagerCompat managerCompat;
    Settings settings;

    /**
     * Встроенное расписание (от сюда можно импортировать расписания в своё расписание,
     * и настроить автоматические авто-импортирование(синхронизацию). Ссылки на пресеты которые нужео
     * автоматически синхронизировать находятся в {@link ScheduleInformatorApp#builtinScheduleAutoSyncPresets})
     * **/
    BuiltinSchedule builtinSchedule = new BuiltinSchedule();
    /**
     * @see ScheduleInformatorApp#builtinSchedule
     * **/
    List<UUID> builtinScheduleAutoSyncPresets = new ArrayList<>();
    long builtinScheduleLatestUpdateTime = 0;

    public ScheduleInformatorApp(SchoolGuideApp app) {
        this.app = app;

        appScheduleFile = new File(app.getFilesDir(), "scheduleinformator.app_schedule.json");
        appSchedule = (AppSchedule) DataUtil.load(appScheduleFile, AppSchedule.class);
        saveAppSchedule();

        settings = app.getSettings();

        app.getAndroidContext().startService(new Intent(app.getAndroidContext(), InformatorService.class));
    }

    public void stop() {
       if (informatorService != null) informatorService.stopSelf();
    }

    public void saveAll() {
        saveAppSchedule();
    }

    public void saveAppSchedule() {
        DataUtil.save(appScheduleFile, appSchedule);
    }

    public int tick() {
        if (!settings.isScheduleInformatorEnabled) {
            stopForeground();
            return 3000;
        }
        CompressedEvent nowEvent = appSchedule.getSelectedPreset().getNowCompressedEvent();
        CompressedEvent nextEvent = appSchedule.getSelectedPreset().getNextCompressedEvent();
        boolean isNow = nowEvent != null;
        boolean isNext = nextEvent != null;

        if (!isNow && !isNext) {
            stopForeground();
            return 3000;
        }

        if (!isNow && nextEvent.remainsUntilStart() > settings.scheduleNotifyBeforeTime) {
            stopForeground();
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

        managerCompat.notify(NOTIFICATION_ID, notification.toNotification(informatorService, NOTIFICATION_CHANNEL_ID));

        return 1000;
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

    public File getAppScheduleFile() {
        return appScheduleFile;
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

    public AppSchedule getAppSchedule() {
        return appSchedule;
    }
}
