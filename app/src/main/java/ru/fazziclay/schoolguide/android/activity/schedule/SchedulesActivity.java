package ru.fazziclay.schoolguide.android.activity.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.databinding.ActivitySchedulesBinding;

public class SchedulesActivity extends AppCompatActivity {
    CrashReport crashReport;
    ActivitySchedulesBinding binding;
    ScheduleProvider scheduleProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crashReport = new CrashReport(CrashReport.getFolder(this));
        try {
            binding = ActivitySchedulesBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            scheduleProvider = ForegroundService.getInstance().getScheduleProvider();

            initLayout();
        } catch (Throwable throwable) {
            crashReport.error(throwable);
            crashReport.notifyUser(this);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLayout();
    }

    private void initLayout() {
        // Init schedules list
        UUID[] schedulesIds = scheduleProvider.getAllSchedules();
        List<String> names = new ArrayList<>();

        for (UUID uuid : schedulesIds) {
            LocalSchedule localSchedule = scheduleProvider.getLocalSchedule(uuid);
            names.add(localSchedule.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, names);
        binding.schedulesList.setAdapter(adapter);
        binding.schedulesList.setOnItemClickListener((adapterView, view, itemPosition, l) -> {
            Intent intent = new Intent(this, ScheduleEditActivity.class)
                    .putExtra(ScheduleEditActivity.KEY_LOCAL_SCHEDULE_UUID, schedulesIds[itemPosition].toString());
            startActivity(intent);
        });

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

                        Intent intent = new Intent(this, ScheduleEditActivity.class)
                                .putExtra(ScheduleEditActivity.KEY_LOCAL_SCHEDULE_UUID, scheduleProvider.addLocalSchedule(localSchedule).toString());
                        startActivity(intent);
                    })
                    .setNegativeButton(R.string.abc_cancel, (dialogInterface, i) -> {

                    });

            builder.show();
        });
    }

    private LocalSchedule copyLocalSchedule(LocalSchedule localSchedule) {
        Class<? extends LocalSchedule> l = LocalSchedule.class;
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(localSchedule, l), l);
    }
}