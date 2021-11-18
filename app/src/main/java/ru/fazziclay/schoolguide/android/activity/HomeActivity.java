package ru.fazziclay.schoolguide.android.activity;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.activity.lesson.LessonsActivity;
import ru.fazziclay.schoolguide.android.activity.schedule.ScheduleEditActivity;
import ru.fazziclay.schoolguide.android.activity.schedule.SchedulesActivity;
import ru.fazziclay.schoolguide.android.activity.settings.SettingsActivity;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;
import ru.fazziclay.schoolguide.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    CrashReport crashReport;
    ActivityHomeBinding binding;
    ScheduleProvider scheduleProvider;
    SettingsProvider settingsProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crashReport = new CrashReport(CrashReport.getFolder(this));
        try {
            binding = ActivityHomeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            scheduleProvider = ForegroundService.getInstance().getScheduleProvider();
            settingsProvider = ForegroundService.getInstance().getSettingsProvider();
            initTheme();
            initLayout();
            initSchedulesLayout();

        } catch (Throwable throwable) {
            crashReport.error(throwable);
            crashReport.notifyUser(this);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initSchedulesLayout();
        } catch (Throwable throwable) {
            crashReport.error(throwable);
            crashReport.notifyUser(this);
            finish();
        }
    }

    private void initTheme() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
    }


    // ! private void initTheme() {
    //    UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
    //    if (settingsProvider.getTheme() == AppTheme.AUTO)
    //        uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_AUTO);
    //    if (settingsProvider.getTheme() == AppTheme.NIGHT)
    //        uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
    //    if (settingsProvider.getTheme() == AppTheme.LIGHT)
    //        uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
    //}
    private void initLayout() {
        binding.settingsButton.setOnClickListener(ignore -> startActivity(new Intent(this, SettingsActivity.class)));
        binding.lessonsButton.setOnClickListener(ignore -> startActivity(new Intent(this, LessonsActivity.class)));
        //binding.scheduleButton.setOnClickListener(ignore -> startActivity(new Intent(this, SchedulesActivity.class)));
    }

    private void initSchedulesLayout() {
        // Init schedules list
        UUID[] schedulesIds = scheduleProvider.getAllSchedules();
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

        // Fab (Floating action button)
        binding.addScheduleButton.setOnClickListener(ignore -> {
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
                            localSchedule = copyLocalSchedule(scheduleProvider.getLocalSchedule(schedules[selected]));
                            localSchedule.setName(getString(R.string.schedules_createNew_copyOf, localSchedule.getName()));
                        }

                        UUID createdUUID = scheduleProvider.addLocalSchedule(localSchedule);
                        if (scheduleProvider.getAllSchedules().length == 0) {
                            SettingsProvider settingsProvider = ForegroundService.getInstance().getSettingsProvider();
                            settingsProvider.setSelectedLocalSchedule(createdUUID);
                        }
                        Intent intent = new Intent(this, ScheduleEditActivity.class)
                                .putExtra(ScheduleEditActivity.KEY_LOCAL_SCHEDULE_UUID, createdUUID.toString());
                        startActivity(intent);
                    })
                    .setNegativeButton(R.string.abc_cancel, null);

            builder.show();
        });
    }

    private LocalSchedule copyLocalSchedule(LocalSchedule localSchedule) {
        Class<? extends LocalSchedule> l = LocalSchedule.class;
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(localSchedule, l), l);
    }
}