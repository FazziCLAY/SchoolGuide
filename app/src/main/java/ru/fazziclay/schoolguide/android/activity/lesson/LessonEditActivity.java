package ru.fazziclay.schoolguide.android.activity.lesson;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.data.schedule.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.databinding.ActivityLessonEditBinding;

public class LessonEditActivity extends AppCompatActivity {
    public static final String KEY_LESSON_INFO_UUID = "lessonInfoUUID";
    public static final String KEY_CREATING_MODE = "isCreating";

    ActivityLessonEditBinding binding;
    ScheduleProvider scheduleProvider;
    UUID lessonInfoUUID;
    LessonInfo lessonInfo;
    boolean isCreatingMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivityLessonEditBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            scheduleProvider = SchoolGuide.getInstance().getScheduleProvider();
            isCreatingMode = getIntent().getExtras().getBoolean(KEY_CREATING_MODE, true);
            if (!isCreatingMode) {
                lessonInfoUUID = UUID.fromString(getIntent().getExtras().getString(KEY_LESSON_INFO_UUID));
                lessonInfo = scheduleProvider.getLessonInfo(lessonInfoUUID);
            }

            initLayout();
        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
            finish();
        }
    }

    private void initLayout() {
        if (lessonInfo != null) {
            binding.lessonName.setText(lessonInfo.getName());
        }

        binding.deleteButton.setVisibility(isCreatingMode ? View.GONE : View.VISIBLE);
        binding.deleteButton.setOnClickListener(ignore -> delete());

        binding.saveButton.setOnClickListener(ignore -> save());
    }

    private void delete() {
        if (SchoolGuide.getInstance().getSettingsProvider().isSyncDeveloperSchedule()) {
            SchoolGuide.showWarnSyncDeveloperScheduleDialog(this);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.lessonEdit_delete_title)
                .setMessage(R.string.lessonEdit_delete_message)
                .setPositiveButton(R.string.abc_delete, (ignore1, ignore2) -> {
                    scheduleProvider.removeLessonInfo(lessonInfoUUID);
                    finish();
                })
                .setNegativeButton(R.string.abc_cancel, null);

        builder.show();
    }

    private void save() {
        if (SchoolGuide.getInstance().getSettingsProvider().isSyncDeveloperSchedule()) {
            SchoolGuide.showWarnSyncDeveloperScheduleDialog(this);
            return;
        }

        Editable editable = binding.lessonName.getText();
        if (editable == null || editable.toString().isEmpty()) {
            notify(R.string.abc_fillAllFields);
            return;
        }
        String name = editable.toString();

        if (isCreatingMode) {
            lessonInfo = new LessonInfo(name);
            lessonInfoUUID = scheduleProvider.addLessonInfo(lessonInfo);
        }

        lessonInfo.setName(name);
        scheduleProvider.save();
        finish();
    }

    private void notify(int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}