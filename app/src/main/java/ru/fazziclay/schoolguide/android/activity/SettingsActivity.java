package ru.fazziclay.schoolguide.android.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.fazziclay.schoolguide.data.Settings;
import ru.fazziclay.schoolguide.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.notification.setChecked(Settings.getSettings().notification);
        binding.vibration.setChecked(Settings.getSettings().vibration);
        binding.useForegroundNotificationForMain.setChecked(Settings.getSettings().useForegroundNotificationForMain);

        binding.notification.setOnClickListener(v -> {
            Settings.getSettings().notification = binding.notification.isChecked();
            Settings.save(this);
        });
        binding.vibration.setOnClickListener(v -> {
            Settings.getSettings().vibration = binding.vibration.isChecked();
            Settings.save(this);
        });
        binding.useForegroundNotificationForMain.setOnClickListener(v -> {
            Settings.getSettings().useForegroundNotificationForMain = binding.useForegroundNotificationForMain.isChecked();
            Settings.save(this);
        });
    }
}