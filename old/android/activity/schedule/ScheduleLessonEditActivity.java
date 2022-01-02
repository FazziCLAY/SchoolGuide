package ru.fazziclay.schoolguide.android.activity.schedule;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.android.activity.lesson.LessonEditActivity;
import ru.fazziclay.schoolguide.data.schedule.Lesson;
import ru.fazziclay.schoolguide.data.schedule.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.databinding.ActivityScheduleLessonEditBinding;
import ru.fazziclay.schoolguide.databinding.BigNotificationBinding;
import ru.fazziclay.schoolguide.util.TimeUtil;

public class ScheduleLessonEditActivity extends AppCompatActivity {
    public static final String KEY_LOCAL_SCHEDULE_UUID = "localScheduleUUID"; // uuid локального расписания на который нацелен этот активити
    public static final String KEY_LOCAL_SCHEDULE_EDIT_DAY_OF_WEEK = "localScheduleEditDayOfWeek"; // день недели (Calendar.MONDAY) в на который нацелен активити
    public static final String KEY_LESSON_POSITION = "lessonPosition"; // Позиция редактируемого урока в неделе

    SchoolGuide app;

    ActivityScheduleLessonEditBinding binding;

    ScheduleProvider scheduleProvider = null; // для отображения доп. штук и сохранения
    UUID localScheduleUUID = null; // Полученный UUID локального расписания
    LocalSchedule localSchedule = null; // Полученное из UUID расписание (из scheduleProvider)
    int dayOfWeek = 0; // Полученный день недели в формате (Calendar.MONDAY)
    List<Lesson> dayOfWeekLessons = null; // День недели из локального расписани полученный из scheduleProvider путём dayOfWeek
    int lessonPosition = 0; // Полученное положение урока в неделе уроков
    Lesson lesson = null; // Редактируемый урок полученный путём lessonPosition

    boolean isCreatingMode = false; // Режим ли создания? Если да то при сохранении будет создан новый

    // Для выподающего списка выбора урока (LessonInfo)
    ArrayAdapter<String> lessonAdapter = null;
    UUID[] lessonAdapterValues = null;
    int selectedLesson = 0;

    // Новые временные значения, при save() они пойдут в lesson, в начале такие же как и в lesson
    UUID lessonInfoUUID = null;
    int startTime = 0;
    int duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            app = SchoolGuide.get(this);
            if (inputInit()) {
                finish();
                return;
            }

            if (scheduleProvider.getAllLessons().length == 0) {
                BigNotificationBinding bigNotificationBinding = BigNotificationBinding.inflate(getLayoutInflater());
                setContentView(bigNotificationBinding.getRoot());

                bigNotificationBinding.title.setText(R.string.abc_actionBefore);
                bigNotificationBinding.text.setText(R.string.schedule_lesson_lessonsNotFound);

                bigNotificationBinding.actionButton.setText(R.string.abc_create);
                bigNotificationBinding.actionButton.setOnClickListener(ignore -> {
                    startActivity(new Intent(this, LessonEditActivity.class)
                            .putExtra(LessonEditActivity.KEY_CREATING_MODE, true)
                    );
                    finish();
                });
                return;
            }

            binding = ActivityScheduleLessonEditBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
            String localizedDayOfWeek = dateFormatSymbols.getWeekdays()[dayOfWeek].toLowerCase();
            if (isCreatingMode) {
                setTitle(getString(R.string.activityTitle_scheduleLessonEdit_create, localizedDayOfWeek));
            } else {
                setTitle(getString(R.string.activityTitle_scheduleLessonEdit_edit, localizedDayOfWeek));
            }

            init();
            initLayout();
        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
            finish();
        }
    }

    private boolean inputInit() {
        Bundle extras = getIntent().getExtras();
        if (!extras.containsKey(KEY_LOCAL_SCHEDULE_UUID) || !extras.containsKey(KEY_LOCAL_SCHEDULE_EDIT_DAY_OF_WEEK)) {
            Toast.makeText(this, R.string.abc_error, Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        isCreatingMode = true;
        localScheduleUUID = UUID.fromString(extras.getString(KEY_LOCAL_SCHEDULE_UUID));
        dayOfWeek = extras.getInt(KEY_LOCAL_SCHEDULE_EDIT_DAY_OF_WEEK);

        scheduleProvider = app.getSchedule();
        localSchedule = scheduleProvider.getLocalSchedule(localScheduleUUID);
        dayOfWeekLessons = localSchedule.get(dayOfWeek);
        if (extras.containsKey(KEY_LESSON_POSITION)) {
            lessonPosition = extras.getInt(KEY_LESSON_POSITION);
            lesson = dayOfWeekLessons.get(lessonPosition);
            isCreatingMode = false;
        }
        return false;
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

    interface TimePickedInterface {
        void run(int seconds);
    }

    private void timePicker(int defaultTime, String dialogTitle, TimePickedInterface pickedInterface) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (timePicker, hour, minute) -> {
                    int seconds = (hour*60*60) + minute*60;
                    pickedInterface.run(seconds);
                }, TimeUtil.getHoursInSeconds(defaultTime), TimeUtil.getMinutesInSeconds(defaultTime), true);

        timePickerDialog.setMessage(dialogTitle);
        timePickerDialog.show();
    }

    private void delete() {
        if (app.getSettings().isSyncDeveloperSchedule()) {
            SchoolGuide.showWarnSyncDeveloperScheduleDialog(this);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.scheduleLessonEdit_delete_title)
                .setMessage(R.string.scheduleLessonEdit_delete_message)
                .setPositiveButton(R.string.abc_delete, (ignore1, ignore2) -> {
                    dayOfWeekLessons.remove(lesson);
                    scheduleProvider.save();
                    finish();
                })
                .setNegativeButton(R.string.abc_cancel, null);

        builder.show();
    }

    private void save() {
        if (app.getSettings().isSyncDeveloperSchedule()) {
            SchoolGuide.showWarnSyncDeveloperScheduleDialog(this);
            return;
        }

        if (lesson == null) {
            lesson = new Lesson(null, 0, 0);
            dayOfWeekLessons.add(lesson);
        }

        lesson.setLessonInfo(lessonInfoUUID);
        lesson.setStart(startTime);
        lesson.setDuration(duration);
        localSchedule.sortDay(scheduleProvider, dayOfWeekLessons);
        finish();
    }
}