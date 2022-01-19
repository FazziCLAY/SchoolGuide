package ru.fazziclay.schoolguide;

import android.annotation.SuppressLint;
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
    SchoolGuideApp app;

    @SuppressLint("SetTextI18n")
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
        loading.setText("SchoolGuide");
        setContentView(loading);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel schedule = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID, "Schedule Informator", NotificationManager.IMPORTANCE_NONE);
            schedule.setDescription("Информирует о текущем расписании");
            notificationManager.createNotificationChannel(schedule);
        }
        startService(new Intent(this, SchoolGuideService.class));

        app = SchoolGuideApp.get(this);
        //MultiplicationGameActivity.open(this, false);
        startActivity(new Intent(this, PresetListActivity.class));

        finish();
    }
}
