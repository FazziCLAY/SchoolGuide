package ru.fazziclay.schoolguide.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.schedule.info.LessonInfo;
import ru.fazziclay.schoolguide.databinding.ActivityLessonBinding;

public class LessonActivity extends AppCompatActivity {
    public static final String KEY_LESSON_INFO_UUID = "uuid";
    public static final String KEY_CREATING_MODE = "isCreatingMode";

    ActivityLessonBinding binding;
    ScheduleProvider scheduleProvider;
    UUID lessonInfoUUID;
    LessonInfo lessonInfo;
    boolean isCreatingMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLessonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        scheduleProvider = ForegroundService.getInstance().getScheduleProvider();
        isCreatingMode = getIntent().getExtras().getBoolean(KEY_CREATING_MODE);
        if (!isCreatingMode) {
            lessonInfoUUID = UUID.fromString(getIntent().getExtras().getString(KEY_LESSON_INFO_UUID));
            lessonInfo = scheduleProvider.getLessonInfoByUUID(lessonInfoUUID);
        }

        initLayout();
    }

    private void initLayout() {
        if (lessonInfo != null) binding.lessonName.setText(lessonInfo.getName());

        binding.deleteButton.setVisibility(isCreatingMode ? View.GONE : View.VISIBLE);
        binding.deleteButton.setOnClickListener(ignore -> delete());

        binding.saveButton.setOnClickListener(ignore -> save());
    }

    private void delete() {
        scheduleProvider.removeTeacherInfo(lessonInfoUUID);
        scheduleProvider.save();
        finish();
    }

    private void save() {
        String name = binding.lessonName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCreatingMode) {
            lessonInfo = new LessonInfo(name);
            lessonInfoUUID = scheduleProvider.addLessonInfo(lessonInfo);
        }

        lessonInfo.setName(name);
        scheduleProvider.save();
        finish();
    }
}