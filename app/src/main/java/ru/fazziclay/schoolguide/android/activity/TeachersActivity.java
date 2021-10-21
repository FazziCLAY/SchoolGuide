package ru.fazziclay.schoolguide.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.schedule.info.TeacherInfo;
import ru.fazziclay.schoolguide.databinding.ActivityTeachersBinding;

public class TeachersActivity extends AppCompatActivity {
    ActivityTeachersBinding binding;
    ScheduleProvider scheduleProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeachersBinding.inflate(getLayoutInflater());
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
        // Init teachers list
        UUID[] teachersIds = scheduleProvider.getAllTeachersUUID();
        List<String > names = new ArrayList<>();

        for (UUID uuid : teachersIds) {
            TeacherInfo teacherInfo = scheduleProvider.getTeacherInfoByUUID(uuid);
            names.add(teacherInfo.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        binding.teachersList.setAdapter(adapter);
        binding.teachersList.setOnItemClickListener((adapterView, view, i, l) -> {
            UUID clickedTo = teachersIds[i];

            Intent intent = new Intent(this, TeacherActivity.class)
                    .putExtra(TeacherActivity.KEY_TEACHER_INFO_UUID, clickedTo.toString())
                    .putExtra(TeacherActivity.KEY_CREATING_MODE, false);
            startActivity(intent);
        });

        // Fab
        binding.addTeacherButton.setOnClickListener(ignore -> {
            Intent intent = new Intent(this, TeacherActivity.class)
                    .putExtra(TeacherActivity.KEY_CREATING_MODE, true);
            startActivity(intent);
        });
    }
}