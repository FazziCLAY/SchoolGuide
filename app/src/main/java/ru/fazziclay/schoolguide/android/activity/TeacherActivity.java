package ru.fazziclay.schoolguide.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.schedule.info.TeacherInfo;
import ru.fazziclay.schoolguide.databinding.ActivityTeacherBinding;

public class TeacherActivity extends AppCompatActivity {
    public static final String KEY_TEACHER_INFO_UUID = "uuid";
    public static final String KEY_CREATING_MODE = "isCreatingMode";

    ActivityTeacherBinding binding;
    ScheduleProvider scheduleProvider;
    UUID teacherInfoUUID;
    TeacherInfo teacherInfo;
    boolean isCreatingMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        scheduleProvider = ForegroundService.getInstance().getScheduleProvider();
        isCreatingMode = getIntent().getExtras().getBoolean(KEY_CREATING_MODE);
        if (!isCreatingMode) {
            teacherInfoUUID = UUID.fromString(getIntent().getExtras().getString(KEY_TEACHER_INFO_UUID));
            teacherInfo = scheduleProvider.getTeacherInfoByUUID(teacherInfoUUID);
        }

        initLayout();
    }

    private void initLayout() {
        if (teacherInfo != null) binding.teacherName.setText(teacherInfo.getName());

        binding.deleteButton.setVisibility(isCreatingMode ? View.GONE : View.VISIBLE);
        binding.deleteButton.setOnClickListener(ignore -> delete());

        binding.saveButton.setOnClickListener(ignore -> save());
    }

    private void delete() {
        scheduleProvider.removeTeacherInfo(teacherInfoUUID);
        scheduleProvider.save();
        finish();
    }

    private void save() {
        String name = binding.teacherName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCreatingMode) {
            teacherInfo = new TeacherInfo(name);
            teacherInfoUUID = scheduleProvider.addTeacherInfo(teacherInfo);
        }

        teacherInfo.setName(name);
        scheduleProvider.save();
        finish();
    }
}