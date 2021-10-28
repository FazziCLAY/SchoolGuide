package ru.fazziclay.schoolguide.android.activity.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.databinding.ActivityLessonsBinding;

public class LessonsActivity extends AppCompatActivity {
    CrashReport crashReport;
    ActivityLessonsBinding binding;
    ScheduleProvider scheduleProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crashReport = new CrashReport(CrashReport.getFolder(this));
        try {
            binding = ActivityLessonsBinding.inflate(getLayoutInflater());
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
        // Init lessons list
        UUID[] lessonsIds = scheduleProvider.getAllLessons();
        List<String> names = new ArrayList<>();

        for (UUID uuid : lessonsIds) {
            LessonInfo teacherInfo = scheduleProvider.getLessonInfo(uuid);
            names.add(teacherInfo.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, names);
        binding.lessonsList.setAdapter(adapter);
        binding.lessonsList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this, LessonEditActivity.class)
                    .putExtra(LessonEditActivity.KEY_LESSON_INFO_UUID, lessonsIds[i].toString())
                    .putExtra(LessonEditActivity.KEY_CREATING_MODE, false);
            startActivity(intent);
        });

        // Fab
        binding.addLessonButton.setOnClickListener(ignore -> {
            Intent intent = new Intent(this, LessonEditActivity.class)
                    .putExtra(LessonEditActivity.KEY_CREATING_MODE, true);
            startActivity(intent);
        });
    }
}