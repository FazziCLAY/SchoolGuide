package ru.fazziclay.schoolguide.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import ru.fazziclay.schoolguide.LaunchActivity;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;

public class SchoolGuideAndroidApp {
    SchoolGuideApp app;
    Context context;

    public SchoolGuideAndroidApp(SchoolGuideApp app) {
        this.app = app;
    }

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void launch(LaunchActivity launchActivity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            NotificationChannel schedule = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID, "Schedule Informator", NotificationManager.IMPORTANCE_NONE);
            schedule.setDescription("Информирует о текущем расписании");
            notificationManager.createNotificationChannel(schedule);
        }

        launchActivity.startService(new Intent(launchActivity, SchoolGuideService.class));
        app.launch(launchActivity);
        app.getScheduleInformatorApp().launch(launchActivity);
        app.getMultiplicationTreningApp().launch(launchActivity);
    }

    public Context getContext() {
        return context;
    }
}
