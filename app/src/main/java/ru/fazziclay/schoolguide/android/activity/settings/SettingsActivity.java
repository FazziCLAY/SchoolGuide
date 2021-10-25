package ru.fazziclay.schoolguide.android.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;
import ru.fazziclay.schoolguide.data.settings.UserNotification;
import ru.fazziclay.schoolguide.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    SettingsProvider settingsProvider = null;
    ScheduleProvider scheduleProvider = null;

    ArrayAdapter<String> userNotificationAdapter = null;
    UserNotification[] userNotificationAdapterValues = null;

    ArrayAdapter<String> selectedLocalScheduleAdapter = null;
    UUID[] selectedLocalScheduleAdapterValues = null;
    int selectedLocalSchedulePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsProvider = ForegroundService.getInstance().getSettingsProvider();
        scheduleProvider = ForegroundService.getInstance().getScheduleProvider();

        initAdapters();
        initLayout();
    }

    private void initAdapters() {
        userNotificationAdapterValues = new UserNotification[]{UserNotification.FOREGROUND, UserNotification.EXTERNAL};
        userNotificationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{getString(R.string.userNotification_foreground), getString(R.string.userNotification_external)});


        selectedLocalScheduleAdapterValues = scheduleProvider.getAllSchedules();
        List<String> names = new ArrayList<>();

        int i = 0;
        for (UUID uuid : selectedLocalScheduleAdapterValues) {
            LocalSchedule localSchedule = scheduleProvider.getLocalSchedule(uuid);
            names.add(localSchedule.getName());
            if (settingsProvider.getSelectedLocalSchedule() == uuid) selectedLocalSchedulePosition = i;
        }

        selectedLocalScheduleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names);
    }

    private void initLayout() {
        // Notification
        binding.isNotificationCheckbox.setChecked(settingsProvider.isNotification());
        binding.isNotificationCheckbox.setOnClickListener(ignore -> {
            settingsProvider.setNotification(binding.isNotificationCheckbox.isChecked());
            initUserNotificationSpinner();
        });

        initUserNotificationSpinner();
        binding.userNotificationSpinner.setAdapter(userNotificationAdapter);
        binding.userNotificationSpinner.setSelection(settingsProvider.getUserNotification() == UserNotification.FOREGROUND ? 0 : 1);
        binding.userNotificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settingsProvider.setUserNotification(userNotificationAdapterValues[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                binding.userNotificationSpinner.setPrompt("No selected!");
            }
        });

        // Vibration
        binding.isVibrationCheckbox.setChecked(settingsProvider.isVibration());
        binding.isVibrationCheckbox.setOnClickListener(ignore -> settingsProvider.setVibration(binding.isVibrationCheckbox.isChecked()));

        // Selected local Schedule
        binding.selectedLocalSchedule.setAdapter(selectedLocalScheduleAdapter);
        binding.selectedLocalSchedule.setSelection(selectedLocalSchedulePosition);
        binding.selectedLocalSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settingsProvider.setSelectedLocalSchedule(selectedLocalScheduleAdapterValues[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                binding.selectedLocalSchedule.setPrompt("No selected!");
            }
        });
    }

    private void initUserNotificationSpinner() {
        binding.userNotificationSpinner.setEnabled(settingsProvider.isNotification());
    }
}