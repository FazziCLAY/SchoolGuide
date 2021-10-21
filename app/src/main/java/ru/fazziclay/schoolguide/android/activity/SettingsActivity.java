package ru.fazziclay.schoolguide.android.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;
import ru.fazziclay.schoolguide.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    SettingsProvider settingsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsProvider = ForegroundService.getInstance().getSettingsProvider(); // TODO: 21-Oct-21 create provider center

        initLayout();
    }

    private void initLayout() {
        binding.notification.setChecked(settingsProvider.isNotification());
        binding.notification.setOnClickListener(ignore -> settingsProvider.setNotification(binding.notification.isChecked()));

        binding.vibration.setChecked(settingsProvider.isVibration());
        binding.vibration.setOnClickListener(ignore -> settingsProvider.setVibration(binding.vibration.isChecked()));

        binding.useForegroundNotificationForMain.setChecked(settingsProvider.isUseForegroundNotificationForMain());
        binding.useForegroundNotificationForMain.setOnClickListener(ignore -> settingsProvider.setUseForegroundNotificationForMain(binding.useForegroundNotificationForMain.isChecked()));
    }
}