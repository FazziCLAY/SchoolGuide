package ru.fazziclay.schoolguide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.Settings;
import ru.fazziclay.schoolguide.databinding.SettingsActivityBinding;

public class SettingsActivity extends AppCompatActivity {
    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    private SchoolGuideApp app;
    private Settings settings;
    private SettingsActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        if (app == null) {
            setContentView(SharedConstrains.getAppNullView(this));
            return;
        }
        settings = app.getSettings();

        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setActualState(settings);
        setCallbacks();
    }

    private void setActualState(Settings settings) {
        binding.developerFeatures.setChecked(settings.developerFeatures);
        binding.stopForegroundIsNone.setChecked(settings.stopForegroundIsNone);
        binding.isFirstMonday.setChecked(settings.isFirstMonday);
    }

    private void setCallbacks() {
        binding.developerFeatures.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.developerFeatures = isChecked;
            save();
        });

        binding.stopForegroundIsNone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.stopForegroundIsNone = isChecked;
            save();
        });

        binding.isFirstMonday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.isFirstMonday = isChecked;
            save();
        });

        binding.changeScheduleNotifyBeforeTime.setOnClickListener(v -> {
            EditText editText = new EditText(this);
            editText.setText(String.valueOf(settings.scheduleNotifyBeforeTime));

            new AlertDialog.Builder(this)
                    .setView(editText)
                    .setPositiveButton("APPLY", (ig, ign) -> {
                        try {
                            settings.scheduleNotifyBeforeTime = Integer.parseInt(editText.getText().toString());
                        } catch (Exception e) {
                            app.getAppTrace().point("changeScheduleNotifyBeforeTimeDialog", e);
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        }
                        save();
                    })
                    .show();
        });
    }

    private void save() {
        app.saveSettings();
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Saved!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}