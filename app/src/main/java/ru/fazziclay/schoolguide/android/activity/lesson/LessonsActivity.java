package ru.fazziclay.schoolguide.android.activity.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.data.schedule.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.databinding.ActivityLessonsBinding;

public class LessonsActivity extends AppCompatActivity {
    ActivityLessonsBinding binding;
    ScheduleProvider scheduleProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivityLessonsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            scheduleProvider = SchoolGuide.getInstance().getScheduleProvider();

            initLayout();
        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initLayout();
        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
            finish();
        }
    }

    private void initLayout() {
        UUID[] lessonsIds = scheduleProvider.getAllLessons();
        List<String> names = new ArrayList<>();

        for (UUID uuid : lessonsIds) {
            LessonInfo teacherInfo = scheduleProvider.getLessonInfo(uuid);
            names.add(teacherInfo.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, names);
        binding.lessonsList.setAdapter(adapter);
        binding.lessonsList.setOnItemClickListener((adapterView, view, i, l) -> {
            if (SchoolGuide.getInstance().getSettingsProvider().isSyncDeveloperSchedule()) {
                SchoolGuide.showWarnSyncDeveloperScheduleDialog(this);
                return;
            }

            Intent intent = new Intent(this, LessonEditActivity.class)
                    .putExtra(LessonEditActivity.KEY_LESSON_INFO_UUID, lessonsIds[i].toString())
                    .putExtra(LessonEditActivity.KEY_CREATING_MODE, false);
            startActivity(intent);
        });

        // Fab
        binding.addLessonButton.setOnClickListener(ignore -> {
            if (SchoolGuide.getInstance().getSettingsProvider().isSyncDeveloperSchedule()) {
                SchoolGuide.showWarnSyncDeveloperScheduleDialog(this);
                return;
            }
            Intent intent = new Intent(this, LessonEditActivity.class)
                    .putExtra(LessonEditActivity.KEY_CREATING_MODE, true);
            startActivity(intent);
        });
    }
}