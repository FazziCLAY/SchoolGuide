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
import ru.fazziclay.schoolguide.data.schedule.info.LessonInfo;
import ru.fazziclay.schoolguide.databinding.ActivityLessonsBinding;

public class LessonsActivity extends AppCompatActivity {
    ActivityLessonsBinding binding;
    ScheduleProvider scheduleProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLessonsBinding.inflate(getLayoutInflater());
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
        // Init lessons list
        UUID[] lessonsIds = scheduleProvider.getAllLessonsUUID();
        List<String > names = new ArrayList<>();

        for (UUID uuid : lessonsIds) {
            LessonInfo teacherInfo = scheduleProvider.getLessonInfoByUUID(uuid);
            names.add(teacherInfo.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        binding.lessonsList.setAdapter(adapter);
        binding.lessonsList.setOnItemClickListener((adapterView, view, i, l) -> {
            UUID clickedTo = lessonsIds[i];

            Intent intent = new Intent(this, LessonActivity.class)
                    .putExtra(LessonActivity.KEY_LESSON_INFO_UUID, clickedTo.toString())
                    .putExtra(LessonActivity.KEY_CREATING_MODE, false);

            startActivity(intent);
        });

        // Fab
        binding.addLessonButton.setOnClickListener(ignore -> {
            Intent intent = new Intent(this, LessonActivity.class)
                    .putExtra(LessonActivity.KEY_CREATING_MODE, true);
            startActivity(intent);
        });
    }
}