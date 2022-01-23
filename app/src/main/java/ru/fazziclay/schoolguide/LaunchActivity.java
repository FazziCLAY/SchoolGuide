package ru.fazziclay.schoolguide;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.SchoolGuideService;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.android.PresetListActivity;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } catch (Exception ignored) {
        }

        TextView loading = new TextView(this);
        loading.setTextSize(40);
        loading.setGravity(Gravity.CENTER);
        loading.setText(R.string.application_name);
        setContentView(loading);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            NotificationChannel scheduleInformatorNone = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NONE, getString(R.string.notificationChannel_scheduleInformator_scheduleNone_name), NotificationManager.IMPORTANCE_DEFAULT);
            scheduleInformatorNone.setDescription(getString(R.string.notificationChannel_scheduleInformator_scheduleNone_description));

            NotificationChannel scheduleInformatorNext = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NEXT, getString(R.string.notificationChannel_scheduleInformator_scheduleNext_name), NotificationManager.IMPORTANCE_DEFAULT);
            scheduleInformatorNext.setDescription(getString(R.string.notificationChannel_scheduleInformator_scheduleNext_description));

            NotificationChannel scheduleInformatorNow = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NOW, getString(R.string.notificationChannel_scheduleInformator_scheduleNow_name), NotificationManager.IMPORTANCE_DEFAULT);
            scheduleInformatorNow.setDescription(getString(R.string.notificationChannel_scheduleInformator_scheduleNow_description));

            NotificationChannel updateCenter = new NotificationChannel(UpdateCenterActivity.NOTIFICATION_CHANNEL_ID, getString(R.string.notificationChannel_updateCenter_name), NotificationManager.IMPORTANCE_DEFAULT);
            updateCenter.setDescription(getString(R.string.notificationChannel_updateCenter_description));

            notificationManager.createNotificationChannel(scheduleInformatorNone);
            notificationManager.createNotificationChannel(scheduleInformatorNext);
            notificationManager.createNotificationChannel(scheduleInformatorNow);
            notificationManager.createNotificationChannel(updateCenter);
        }

        startService(new Intent(this, SchoolGuideService.class));

        SchoolGuideApp.get(this);
        startActivity(new Intent(this, PresetListActivity.class));

        finish();
    }
}
