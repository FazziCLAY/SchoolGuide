package ru.fazziclay.schoolguide.android.activity;

import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.fazziclay.schoolguide.android.activity.lesson.LessonsActivity;
import ru.fazziclay.schoolguide.android.activity.schedule.SchedulesActivity;
import ru.fazziclay.schoolguide.android.activity.settings.SettingsActivity;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.settings.AppTheme;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;
import ru.fazziclay.schoolguide.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    SettingsProvider settingsProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsProvider = ForegroundService.getInstance().getSettingsProvider();

        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (settingsProvider.getTheme() == AppTheme.AUTO) uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_AUTO);
        if (settingsProvider.getTheme() == AppTheme.NIGHT) uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
        if (settingsProvider.getTheme() == AppTheme.LIGHT) uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);

        initLayout();
    }

    private void initLayout() {
        binding.settingsButton.setOnClickListener(ignore -> startActivity(new Intent(this, SettingsActivity.class)));
        binding.lessonsButton.setOnClickListener(ignore -> startActivity(new Intent(this, LessonsActivity.class)));
        binding.scheduleButton.setOnClickListener(ignore -> startActivity(new Intent(this, SchedulesActivity.class)));
    }
}