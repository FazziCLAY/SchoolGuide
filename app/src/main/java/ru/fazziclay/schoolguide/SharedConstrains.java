package ru.fazziclay.schoolguide;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;

public class SharedConstrains {
    public static final int APPLICATION_VERSION_CODE = BuildConfig.VERSION_CODE;
    public static final String APPLICATION_VERSION_NAME = BuildConfig.VERSION_NAME;
    public static final String APPLICATION_ID = BuildConfig.APPLICATION_ID;
    public static final String APPLICATION_BUILD_TYPE = BuildConfig.BUILD_TYPE;

    // TODO: 2022-01-26 change to main branch
    public static final String KEYS_V2 = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/dev/v34/manifest/v2/keys_v2.json";
    public static final String VERSION_MANIFEST_V2 = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/dev/v34/manifest/v2/version_manifest_v2.json";
    public static final String BUILTIN_SCHEDULE_V2 = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/dev/v34/manifest/v2/builtin_schedule_v2.json";

    /**
     * Зарегистрировать каналы уведомлений для андроида
     * **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void registerAndroidNotificationChannels(Context context) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        NotificationChannel scheduleInformatorNone = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NONE, context.getString(R.string.notificationChannel_scheduleInformator_scheduleNone_name), NotificationManager.IMPORTANCE_DEFAULT);
        scheduleInformatorNone.setDescription(context.getString(R.string.notificationChannel_scheduleInformator_scheduleNone_description));

        NotificationChannel scheduleInformatorNext = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NEXT, context.getString(R.string.notificationChannel_scheduleInformator_scheduleNext_name), NotificationManager.IMPORTANCE_DEFAULT);
        scheduleInformatorNext.setDescription(context.getString(R.string.notificationChannel_scheduleInformator_scheduleNext_description));

        NotificationChannel scheduleInformatorNow = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NOW, context.getString(R.string.notificationChannel_scheduleInformator_scheduleNow_name), NotificationManager.IMPORTANCE_DEFAULT);
        scheduleInformatorNow.setDescription(context.getString(R.string.notificationChannel_scheduleInformator_scheduleNow_description));

        NotificationChannel updateCenter = new NotificationChannel(UpdateCenterActivity.NOTIFICATION_CHANNEL_ID, context.getString(R.string.notificationChannel_updateCenter_name), NotificationManager.IMPORTANCE_DEFAULT);
        updateCenter.setDescription(context.getString(R.string.notificationChannel_updateCenter_description));

        notificationManager.createNotificationChannel(scheduleInformatorNone);
        notificationManager.createNotificationChannel(scheduleInformatorNext);
        notificationManager.createNotificationChannel(scheduleInformatorNow);
        notificationManager.createNotificationChannel(updateCenter);
    }
}
