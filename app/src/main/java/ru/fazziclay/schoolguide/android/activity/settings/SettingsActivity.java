package ru.fazziclay.schoolguide.android.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.SpinnerAdapter;
import ru.fazziclay.schoolguide.android.activity.UpdateCheckerActivity;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;
import ru.fazziclay.schoolguide.data.settings.UserNotification;
import ru.fazziclay.schoolguide.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    CrashReport crashReport;
    ActivitySettingsBinding binding;
    SettingsProvider settingsProvider = null;
    ScheduleProvider scheduleProvider = null;

    SpinnerAdapter userNotificationAdapter = null;
    SpinnerAdapter selectedLocalScheduleAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crashReport = new CrashReport(CrashReport.getFolder(this));
        try {
            binding = ActivitySettingsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            settingsProvider = ForegroundService.getInstance().getSettingsProvider();
            scheduleProvider = ForegroundService.getInstance().getScheduleProvider();

            initLayout();
        } catch (Throwable throwable) {
            crashReport.error(throwable);
            crashReport.notifyUser(this);
            finish();
        }
    }

    private void initLayout() {
        // Notification
        binding.isNotification.setChecked(settingsProvider.isNotification());
        binding.isNotification.setOnClickListener(checkbox -> {
            settingsProvider.setNotification(((CheckBox)checkbox).isChecked());
            binding.userNotification.setEnabled(settingsProvider.isNotification());
        });

        // Notification spinner
        initUserNotificationSpinner();

        // Vibration
        binding.isVibration.setChecked(settingsProvider.isVibration());
        binding.isVibration.setOnClickListener(checkbox -> settingsProvider.setVibration(((CheckBox)checkbox).isChecked()));

        // Selected local Schedule
        initSelectedLocalScheduleSpinner();

        // Check update
        binding.checkUpdate.setOnClickListener(ignore -> startActivity(new Intent(this, UpdateCheckerActivity.class)));
    }

    private void initUserNotificationSpinner() {
        // Adapter
        List<SpinnerAdapter.SpinnerAdapterElement> userNotificationElements = new ArrayList<>();
        userNotificationElements.add(new SpinnerAdapter.SpinnerAdapterElement(getString(R.string.userNotification_foreground), UserNotification.FOREGROUND));
        userNotificationElements.add(new SpinnerAdapter.SpinnerAdapterElement(getString(R.string.userNotification_external), UserNotification.EXTERNAL));
        userNotificationAdapter = new SpinnerAdapter(userNotificationElements, settingsProvider.getUserNotification());

        // Layout
        binding.userNotification.setEnabled(settingsProvider.isNotification());

        binding.userNotification.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, userNotificationAdapter.getNames()));
        binding.userNotification.setSelection(userNotificationAdapter.getSelected());
        binding.userNotification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settingsProvider.setUserNotification((UserNotification) userNotificationAdapter.getValue(i));
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void initSelectedLocalScheduleSpinner() {
        // Adapter
        List<SpinnerAdapter.SpinnerAdapterElement> selectedLocalScheduleElements = new ArrayList<>();
        for (UUID uuid : scheduleProvider.getAllSchedules()) selectedLocalScheduleElements.add(new SpinnerAdapter.SpinnerAdapterElement(scheduleProvider.getLocalSchedule(uuid).getName(), uuid));
        selectedLocalScheduleAdapter = new SpinnerAdapter(selectedLocalScheduleElements, settingsProvider.getSelectedLocalSchedule());

        // Layout
        binding.selectedLocalSchedule.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, selectedLocalScheduleAdapter.getNames()));
        binding.selectedLocalSchedule.setSelection(selectedLocalScheduleAdapter.getSelected());
        TextView emptyText = new TextView(SettingsActivity.this);
        emptyText.setText(R.string.settings_selectedLocalSchedule_empty);
        binding.selectedLocalSchedule.setEmptyView(emptyText);
        binding.selectedLocalSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settingsProvider.setSelectedLocalSchedule((UUID) selectedLocalScheduleAdapter.getValue(i));
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
}