package ru.fazziclay.schoolguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.SchoolGuideService;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.android.PresetListActivity;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.datafixer.schem.v33to35.SchemePre36To36;

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

            NotificationChannel scheduleInformatorNone = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NONE, "Schedule (None)", NotificationManager.IMPORTANCE_DEFAULT);
            scheduleInformatorNone.setDescription("Информирует о том что сейчас можно отдыхать");

            NotificationChannel scheduleInformatorNext = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NEXT, "Schedule (Next)", NotificationManager.IMPORTANCE_DEFAULT);
            scheduleInformatorNone.setDescription("Информирует о том что скоро будет урок");

            NotificationChannel scheduleInformatorNow = new NotificationChannel(ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NOW, "Schedule (Now)", NotificationManager.IMPORTANCE_DEFAULT);
            scheduleInformatorNone.setDescription("Информирует о том что сейчас идёт урок");

            notificationManager.createNotificationChannel(scheduleInformatorNone); // TODO: 2022-01-19 make translatable
            notificationManager.createNotificationChannel(scheduleInformatorNext);
            notificationManager.createNotificationChannel(scheduleInformatorNow);
        }

        startService(new Intent(this, SchoolGuideService.class));

        app = SchoolGuideApp.get(this);
        startActivity(new Intent(this, PresetListActivity.class)); // TODO: 2022-01-21 change

        finish();
    }
}
