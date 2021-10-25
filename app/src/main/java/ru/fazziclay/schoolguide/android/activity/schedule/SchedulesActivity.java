package ru.fazziclay.schoolguide.android.activity.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.databinding.ActivitySchedulesBinding;

public class SchedulesActivity extends AppCompatActivity {
    ActivitySchedulesBinding binding;
    ScheduleProvider scheduleProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchedulesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        scheduleProvider = ForegroundService.getInstance().getScheduleProvider();

        initLayout();
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
        binding.schedulesList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this, ScheduleEditActivity.class)
                    .putExtra(ScheduleEditActivity.KEY_LOCAL_SCHEDULE_UUID, schedulesIds[i].toString());
            startActivity(intent);
        });

        // Fab
        binding.addScheduleButton.setOnClickListener(ignore -> {
            UUID newUUID = scheduleProvider.addLocalSchedule(new LocalSchedule("New local schedule"));
            Intent intent = new Intent(this, ScheduleEditActivity.class)
                    .putExtra(ScheduleEditActivity.KEY_LOCAL_SCHEDULE_UUID, newUUID.toString());
            startActivity(intent);
        });
    }
}