package ru.fazziclay.schoolguide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.Settings;
import ru.fazziclay.schoolguide.databinding.SettingsActivityBinding;

public class SettingsActivity extends AppCompatActivity {
    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    SchoolGuideApp app;
    Settings settings;
    SettingsActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = SchoolGuideApp.get(this);
        settings = app.getSettings();

        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.developerFeatures.setChecked(settings.developerFeatures);
        binding.developerFeatures.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.developerFeatures = isChecked;
            app.saveSettings();
        });

        binding.stopForegroundIsNone.setChecked(settings.stopForegroundIsNone);
        binding.stopForegroundIsNone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.stopForegroundIsNone = isChecked;
            app.saveSettings();
        });

        binding.isFirstMonday.setChecked(settings.isFirstMonday);
        binding.isFirstMonday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.isFirstMonday = isChecked;
            app.saveSettings();
        });

        binding.changeScheduleNotifyBeforeTime.setOnClickListener(v -> {
            EditText editText = new EditText(this);
            editText.setText(settings.scheduleNotifyBeforeTime+"");

            new AlertDialog.Builder(this)
                    .setView(editText)
                    .setPositiveButton("apply", (ig, ign) -> {
                        settings.scheduleNotifyBeforeTime = Integer.parseInt(editText.getText().toString());
                        app.saveSettings();
                    })
                    .show();
        });
    }
}