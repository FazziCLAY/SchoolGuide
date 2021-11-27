package ru.fazziclay.schoolguide.android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.android.activity.developer.DeveloperActivity;
import ru.fazziclay.schoolguide.android.activity.lesson.LessonsActivity;
import ru.fazziclay.schoolguide.android.activity.schedule.ScheduleEditActivity;
import ru.fazziclay.schoolguide.android.activity.settings.SettingsActivity;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;
import ru.fazziclay.schoolguide.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            binding = ActivityHomeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            initLayout();
            initSchedulesLayout();
            disablePowerSaver();

        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initSchedulesLayout();
        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
            finish();
        }
    }

    private void initLayout() {
        binding.settingsButton.setOnClickListener(ignore -> startActivity(new Intent(this, SettingsActivity.class)));
        binding.lessonsButton.setOnClickListener(ignore -> startActivity(new Intent(this, LessonsActivity.class)));
        binding.addScheduleButton.setOnClickListener(ignore -> showCreateLocalScheduleDialog(SchoolGuide.getInstance().getScheduleProvider()));
        binding.developerScreenButton.setOnClickListener(ignore -> startActivity(new Intent(this, DeveloperActivity.class)));
        binding.developerScreenButton.setVisibility(SchoolGuide.getInstance().getSettingsProvider().isDeveloperFeatures() ? View.VISIBLE : View.GONE);
    }

    private void initSchedulesLayout() {
        ScheduleProvider scheduleProvider = SchoolGuide.getInstance().getScheduleProvider();
        SettingsProvider settingsProvider = SchoolGuide.getInstance().getSettingsProvider();

        // Init schedules list
        UUID[] schedulesIds = scheduleProvider.getAllSchedules();


        if (schedulesIds.length == 0) {
            binding.emptyText.setVisibility(View.VISIBLE);
        } else {
            binding.emptyText.setVisibility(View.GONE);
        }

        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return schedulesIds.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                UUID scheduleUUID = schedulesIds[position];

                CheckBox textView = new CheckBox(HomeActivity.this);
                textView.setPadding(6, 10, 6, 10);
                textView.setTextSize(30);
                textView.setOnCheckedChangeListener((buttonView, isChecked) -> buttonView.setChecked(scheduleUUID.equals(settingsProvider.getSelectedLocalSchedule())));
                textView.setOnClickListener(ignored -> {
                    Intent intent = new Intent(HomeActivity.this, ScheduleEditActivity.class)
                            .putExtra(ScheduleEditActivity.KEY_LOCAL_SCHEDULE_UUID, schedulesIds[position].toString());
                    startActivity(intent);
                });
                textView.setText(scheduleProvider.getLocalSchedule(scheduleUUID).getName());
                textView.setChecked(scheduleUUID.equals(settingsProvider.getSelectedLocalSchedule()));
                return textView;
            }
        };
        binding.schedulesList.setAdapter(adapter);
    }

    // Показать диалог создания (или копирование - по выбору пользовалетя) локального расписания
    private void showCreateLocalScheduleDialog(ScheduleProvider scheduleProvider) {
        if (SchoolGuide.getInstance().getSettingsProvider().isSyncDeveloperSchedule()) {
            SchoolGuide.showWarnSyncDeveloperScheduleDialog(this);
            return;
        }

        List<String> scheduleNames = new ArrayList<>();
        scheduleNames.add(getString(R.string.schedules_createNew_empty));

        UUID[] schedules = scheduleProvider.getAllSchedules();
        for (UUID uuid : schedules) {
            String scheduleName = scheduleProvider.getLocalSchedule(uuid).getName();
            scheduleNames.add(getString(R.string.schedules_createNew_copyOf, scheduleName));
        }

        Spinner spinner = new Spinner(this);
        spinner.setSelection(0);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, scheduleNames));

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.schedules_createNew_title)
                .setView(spinner)
                .setPositiveButton(R.string.abc_create, (dialogInterface, i) -> {
                    LocalSchedule localSchedule = new LocalSchedule(getString(R.string.schedules_createNew_newEmptyName));
                    int selected = spinner.getSelectedItemPosition();
                    if (selected > 0) {
                        selected--;
                        localSchedule = ScheduleProvider.copyLocalSchedule(scheduleProvider.getLocalSchedule(schedules[selected]));
                        localSchedule.setName(getString(R.string.schedules_createNew_copyOf, localSchedule.getName()));
                    }

                    boolean empty = scheduleProvider.getAllSchedules().length == 0;
                    UUID createdUUID = scheduleProvider.addLocalSchedule(localSchedule);
                    if (empty) {
                        SettingsProvider settingsProvider = SchoolGuide.getInstance().getSettingsProvider();
                        settingsProvider.setSelectedLocalSchedule(createdUUID);
                    }
                    Intent intent = new Intent(this, ScheduleEditActivity.class)
                            .putExtra(ScheduleEditActivity.KEY_LOCAL_SCHEDULE_UUID, createdUUID.toString());
                    startActivity(intent);
                })
                .setNegativeButton(R.string.abc_cancel, null);

        builder.show();

    }

    @SuppressLint("BatteryLife")
    private void disablePowerSaver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }
}