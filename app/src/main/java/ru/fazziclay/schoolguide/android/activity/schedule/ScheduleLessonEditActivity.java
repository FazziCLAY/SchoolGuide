package ru.fazziclay.schoolguide.android.activity.schedule;

import android.app.TimePickerDialog;
import java.text.DateFormatSymbols;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.Lesson;
import ru.fazziclay.schoolguide.data.schedule.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.databinding.ActivityScheduleLessonEditBinding;
import ru.fazziclay.schoolguide.util.TimeUtil;

public class ScheduleLessonEditActivity extends AppCompatActivity {
    public static final String KEY_LOCAL_SCHEDULE_UUID = "localScheduleUUID";
    public static final String KEY_LOCAL_SCHEDULE_EDIT_DAY_OF_WEEK = "localScheduleEditDayOfWeek";
    public static final String KEY_LESSON_POSITION = "lessonPosition";
    DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();

    ActivityScheduleLessonEditBinding binding;

    ScheduleProvider scheduleProvider;

    UUID localScheduleUUID = null;
    int dayOfWeek = -1;
    int lessonPosition = -1;
    boolean isCreatingMode = true;

    LocalSchedule localSchedule = null;
    List<Lesson> day = null;
    Lesson lesson = null;

    ArrayAdapter<String> lessonAdapter = null;
    UUID[] lessonAdapterValues = null;
    int selectedLesson = 0;

    UUID lessonInfoUUID = null;
    int startTime = 0;
    int duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleLessonEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            scheduleProvider = ForegroundService.getInstance().getScheduleProvider();

            localScheduleUUID = UUID.fromString(getIntent().getExtras().getString(KEY_LOCAL_SCHEDULE_UUID, new UUID(0, 0).toString()));
            dayOfWeek = getIntent().getExtras().getInt(KEY_LOCAL_SCHEDULE_EDIT_DAY_OF_WEEK, -1);
            lessonPosition = getIntent().getExtras().getInt(KEY_LESSON_POSITION, -1);

            localSchedule = scheduleProvider.getLocalSchedule(localScheduleUUID);
            day = localSchedule.get(dayOfWeek);
        } catch (Throwable throwable) {
            Toast.makeText(this, "Error: "+throwable.toString(), Toast.LENGTH_SHORT).show();
        }

        try {
            lesson = day.get(lessonPosition);
            isCreatingMode = false;
        } catch (Exception ignored) {
            isCreatingMode = true;
        }

        setTitle("SchoolGuide - Create for "+dateFormatSymbols.getWeekdays()[dayOfWeek]);

        init();
        initLayout();
    }

    private void init() {
        if (lesson != null) {
            startTime = lesson.getStart();
            duration = lesson.getDuration();
        }

        lessonAdapterValues = scheduleProvider.getAllLessons();
        List<String> names = new ArrayList<>();
        int i = 0;
        for (UUID uuid : lessonAdapterValues) {
            LessonInfo lessonInfo = scheduleProvider.getLessonInfo(uuid);
            names.add(lessonInfo.getName());
            if (lesson != null) {
                if (uuid.equals(lesson.getLessonInfo())) {
                    selectedLesson = i;
                    lessonInfoUUID = uuid;
                }
            }
            i++;
        }

        if (lesson == null) {
            selectedLesson = 0;
            lessonInfoUUID = lessonAdapterValues[selectedLesson];
        }
        lessonAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names);
    }

    private void initLayout() {
        initStartTimeText();
        initDurationText();

        binding.lesson.setAdapter(lessonAdapter);
        binding.lesson.setSelection(selectedLesson);
        binding.lesson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lessonInfoUUID = lessonAdapterValues[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        binding.deleteButton.setVisibility(isCreatingMode ? View.GONE : View.VISIBLE);
        binding.deleteButton.setOnClickListener(ignore -> delete());

        binding.saveButton.setOnClickListener(ignore -> save());

        binding.startTime.setOnClickListener(ignore -> timePicker(startTime, getString(R.string.schedule_lesson_startTime_title), seconds -> {
            startTime = seconds;
            initStartTimeText();
        }));

        binding.duration.setOnClickListener(ignore -> timePicker(duration, getString(R.string.schedule_lesson_duration_title), seconds -> {
            duration = seconds;
            initDurationText();
        }));
    }

    private void initStartTimeText() {
        binding.startTime.setText(getString(R.string.schedule_lesson_startTime, TimeUtil.secondsToHumanTime(startTime, true)));
    }

    private void initDurationText() {
        binding.duration.setText(getString(R.string.schedule_lesson_duration, TimeUtil.secondsToHumanTime(duration, true)));
    }

    private void timePicker(int defaultTime, String dialogTitle, TimePickedInterface pickedInterface) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (timePicker, hour, minute) -> {
                    int seconds = (hour*60*60) + minute*60;
                    pickedInterface.run(seconds);
                }, TimeUtil.getHoursInSeconds(defaultTime), TimeUtil.getMinutesInSeconds(defaultTime), true);

        timePickerDialog.setTitle(dialogTitle);
        timePickerDialog.show();
    }

    private void delete() {
        day.remove(lesson);
        scheduleProvider.save();
        finish();
    }

    private void save() {
        if (lesson == null) {
            lesson = new Lesson(null, 0, 0);
            day.add(lesson);
        }

        lesson.setLessonInfo(lessonInfoUUID);
        lesson.setStart(startTime);
        lesson.setDuration(duration);
        scheduleProvider.save();
        finish();
    }

    interface TimePickedInterface {
        void run(int seconds);
    }
}