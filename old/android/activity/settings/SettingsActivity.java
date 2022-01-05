package ru.fazziclay.schoolguide.android.activity.settings;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.android.SpinnerAdapter;
import ru.fazziclay.schoolguide.android.activity.UpdateCheckerActivity;
import ru.fazziclay.schoolguide.data.settings.Settings;
import ru.fazziclay.schoolguide.databinding.ActivitySettingsBinding;
import ru.fazziclay.schoolguide.util.TimeUtil;

public class SettingsActivity extends AppCompatActivity {
    SchoolGuide app;
    ActivitySettingsBinding binding;
    SettingsProvider settingsProvider = null;
    ScheduleProvider scheduleProvider = null;

    SpinnerAdapter selectedLocalScheduleAdapter = null;
    SpinnerAdapter notificationClickActionAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            app = SchoolGuide.get(this);
            binding = ActivitySettingsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            settingsProvider = app.getSettings();
            scheduleProvider = app.getSchedule();

            initLayout();
        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
            finish();
        }
    }

    private void initLayout() {
        initNotificationClickActionSpinner();

        // Vibration
        binding.isVibration.setChecked(settingsProvider.isVibration());
        binding.isVibration.setOnClickListener(checkbox -> settingsProvider.setVibration(((CheckBox)checkbox).isChecked()));

        // Selected local Schedule
        initSelectedLocalScheduleSpinner();

        // Check update
        binding.checkUpdate.setOnClickListener(ignore -> startActivity(new Intent(this, UpdateCheckerActivity.class)));

        binding.isSyncDeveloperSchedule.setChecked(settingsProvider.isSyncDeveloperSchedule());
        binding.isSyncDeveloperSchedule.setOnClickListener(checkbox -> {
            if (binding.isSyncDeveloperSchedule.isChecked()) {
                binding.isSyncDeveloperSchedule.setChecked(false);
                AlertDialog.Builder a = new AlertDialog.Builder(this)
                        .setTitle("ВСЁ УДАЛИТСЯ!!!")
                        .setMessage("Эта функция может работать не корректно, т.к. она ещё тестируется!\nФункция эта синхронизирует расписание на вашем телефоне с расписанием на сервере. При включении все ваши расписания буду автоматически ЗАМЕНЯТСЯ теми, что находятся на сервере!\n\nЕсли ваша версия устареет то вам придётся обновится что бы функция работала!")
                        .setPositiveButton("ВКЛючить", (dialog, which) -> {
                            settingsProvider.setSyncDeveloperSchedule(true);
                            binding.isSyncDeveloperSchedule.setChecked(true);
                            app.getManifestProvider().updateForGlobal((e, m) -> app.getSchedule().setSchedule(m.getDeveloperSchedule().copy()));
                        })
                        .setNegativeButton("ВЫКЛючить", (dialog, which) -> {
                            settingsProvider.setSyncDeveloperSchedule(false);
                            binding.isSyncDeveloperSchedule.setChecked(false);
                        });
                a.show();
            } else {
                settingsProvider.setSyncDeveloperSchedule(false);
                binding.isSyncDeveloperSchedule.setChecked(false);
            }
        });

        binding.notifyBeforeTime.setOnClickListener(ignore -> timePicker(settingsProvider.getNotifyBeforeTime(), getString(R.string.settings_notifyBeforeTime_title), seconds -> {
            settingsProvider.setNotifyBeforeTime(seconds);
            initNotifyBeforeTimeText();
        }));
        initNotifyBeforeTimeText();
    }

    private void initNotificationClickActionSpinner() {
        // Adapter
        List<SpinnerAdapter.SpinnerAdapterElement> elements = new ArrayList<>();
        for (Settings.NotificationStyleSettings.NotificationClickAction clickAction : Settings.NotificationStyleSettings.NotificationClickAction.values())
            elements.add(new SpinnerAdapter.SpinnerAdapterElement(getString(clickAction.getTranslate()), clickAction));
        notificationClickActionAdapter = new SpinnerAdapter(elements, settingsProvider.getNotificationStyle().getClickAction());

        binding.notificationStyleClickAction.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, notificationClickActionAdapter.getNames()));
        binding.notificationStyleClickAction.setSelection(notificationClickActionAdapter.getSelected());
        binding.notificationStyleClickAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settingsProvider.getNotificationStyle().setClickAction((Settings.NotificationStyleSettings.NotificationClickAction) notificationClickActionAdapter.getValue(i));
                app.updateNotificationStyle();
                settingsProvider.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void initSelectedLocalScheduleSpinner() {
        // Adapter
        List<SpinnerAdapter.SpinnerAdapterElement> selectedLocalScheduleElements = new ArrayList<>();
        for (UUID uuid : scheduleProvider.getAllSchedules()) selectedLocalScheduleElements.add(new SpinnerAdapter.SpinnerAdapterElement(scheduleProvider.getLocalSchedule(uuid).getName(), uuid));
        selectedLocalScheduleAdapter = new SpinnerAdapter(selectedLocalScheduleElements, settingsProvider.getSelectedLocalSchedule());

        if (selectedLocalScheduleAdapter.isEmpty()) {
            binding.selectedLocalSchedule.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{getString(R.string.settings_selectedLocalSchedule_empty)}));
            binding.selectedLocalSchedule.setSelection(0);

        } else {
            binding.selectedLocalSchedule.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, selectedLocalScheduleAdapter.getNames()));
            binding.selectedLocalSchedule.setSelection(selectedLocalScheduleAdapter.getSelected());
            binding.selectedLocalSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    settingsProvider.setSelectedLocalSchedule((UUID) selectedLocalScheduleAdapter.getValue(i));
                }
                @Override public void onNothingSelected(AdapterView<?> adapterView) {}
            });
        }
    }

    private void initNotifyBeforeTimeText() {
        binding.notifyBeforeTime.setText(getString(R.string.settings_notifyBeforeTime, TimeUtil.secondsToHumanTime(settingsProvider.getNotifyBeforeTime(), true)));
    }

    interface TimePickedInterface {
        void run(int seconds);
    }

    private void timePicker(int defaultTime, String dialogTitle, TimePickedInterface pickedInterface) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (timePicker, hour, minute) -> {
                    int seconds = (hour*60*60) + minute*60;
                    pickedInterface.run(seconds);
                }, TimeUtil.getHoursInSeconds(defaultTime), TimeUtil.getMinutesInSeconds(defaultTime), true);

        timePickerDialog.setMessage(dialogTitle);
        timePickerDialog.show();
    }
}