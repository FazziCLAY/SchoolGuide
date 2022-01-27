package ru.fazziclay.schoolguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.SchoolGuideService;
import ru.fazziclay.schoolguide.app.UpdateCheckerService;
import ru.fazziclay.schoolguide.app.scheduleinformator.android.PresetListActivity;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } catch (Exception ignored) {}

        // UI
        TextView loadingTextView = new TextView(this);
        loadingTextView.setTextSize(40);
        loadingTextView.setGravity(Gravity.CENTER);
        loadingTextView.setText(R.string.application_name);

        setContentView(loadingTextView);

        // Notification channels
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            SchoolGuideApp.registerNotificationChannels(this);
        }

        // Loading
        SchoolGuideApp.get(this);
        startService(new Intent(this, SchoolGuideService.class));
        startService(new Intent(this, UpdateCheckerService.class));

        startActivity(PresetListActivity.getLaunchIntent(this));

        finish();
    }
}
