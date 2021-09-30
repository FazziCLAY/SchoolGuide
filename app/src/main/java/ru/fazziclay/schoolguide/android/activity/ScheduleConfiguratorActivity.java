package ru.fazziclay.schoolguide.android.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.fazziclay.fazziclaylibs.TimeUtil;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.ScheduleData;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.schedule.ScheduledLesson;
import ru.fazziclay.schoolguide.data.schedule.info.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.info.TeacherInfo;
import ru.fazziclay.schoolguide.databinding.ActivityScheduleConfiguratorBinding;

public class ScheduleConfiguratorActivity extends AppCompatActivity {
    ActivityScheduleConfiguratorBinding binding;
    Handler loopHandler = null;
    Runnable loopRunnable = null;
    ScheduleProvider scheduleProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleConfiguratorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        scheduleProvider = ForegroundService.getInstance().getScheduleProvider();

        loopHandler = new Handler(Looper.getMainLooper());
        loopRunnable = new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) return;
                loop();
                loopHandler.postDelayed(this, 1000);
            }
        };
        loopHandler.post(loopRunnable);
    }

    public void loop() {
        binding.teachers.removeAllViews();
        binding.lessons.removeAllViews();
        binding.schedule.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 30);

        List<TeacherInfo> teacherInfoList = scheduleProvider.getTeacherInfoList();
        for (TeacherInfo teacherInfo : teacherInfoList) {
            TextView teacherInfoText = new TextView(this);
            teacherInfoText.setText(teacherInfo.name);
            teacherInfoText.setTextSize(20f);
            teacherInfoText.setBackgroundColor(Color.BLUE);
            teacherInfoText.setLayoutParams(params);
            teacherInfoText.setOnClickListener(v -> onTeacherInfoClick(teacherInfo));
            binding.teachers.addView(teacherInfoText);
        }

        Button addTeacherInfoButton = new Button(this);
        addTeacherInfoButton.setText(R.string.add_teacher);
        addTeacherInfoButton.setTextSize(15f);
        addTeacherInfoButton.setOnClickListener(v -> onAddTeacherInfoClick());
        binding.teachers.addView(addTeacherInfoButton);

        List<LessonInfo> lessonsInfoList = scheduleProvider.getLessonsInfoList();
        for (LessonInfo lessonInfo : lessonsInfoList) {
            TextView teacherInfoText = new TextView(this);
            teacherInfoText.setText(String.format("%s (%s)", lessonInfo.name, scheduleProvider.getTeacherInfoById(lessonInfo.teacher).name));
            teacherInfoText.setTextSize(20f);
            teacherInfoText.setBackgroundColor(Color.BLUE);
            teacherInfoText.setLayoutParams(params);
            teacherInfoText.setOnClickListener(v -> onLessonInfoClick(lessonInfo));
            binding.lessons.addView(teacherInfoText);
        }

        Button addLessonInfoButton = new Button(this);
        addLessonInfoButton.setText(R.string.add_lesson);
        addLessonInfoButton.setTextSize(15f);
        addLessonInfoButton.setOnClickListener(v -> onAddLessonInfoClick());
        binding.lessons.addView(addLessonInfoButton);



        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
        List<List<ScheduledLesson>> list = scheduleProvider.getScheduleWeek().getInList();
        int i = 0;
        while (i < list.size()) {
            int weekIndex = i+2;
            if (weekIndex == 8) weekIndex = 1;
            int finalWeekIndex = weekIndex;

            TextView weekDayTitle = new TextView(this);
            weekDayTitle.setTextSize(19f);
            weekDayTitle.setText(String.format("====== %s ======", dateFormatSymbols.getWeekdays()[weekIndex].toUpperCase()));
            binding.schedule.addView(weekDayTitle);

            List<ScheduledLesson> day = list.get(i);
            int i1 = 0;
            while (i1 < day.size()) {
                ScheduledLesson scheduledLesson = day.get(i1);

                TextView scheduledLessonText = new TextView(this);
                scheduledLessonText.setOnClickListener(v -> onScheduledLessonClick(finalWeekIndex, day, scheduledLesson));
                scheduledLessonText.setBackgroundColor(Color.DKGRAY);
                scheduledLessonText.setTextSize(16f);
                scheduledLessonText.setText(String.format("[%s] [%s - %s] %s (%s)",
                        i1+1,
                        TimeUtil.secondsToDigitalTime(scheduledLesson.startTime, true),
                        TimeUtil.secondsToDigitalTime(scheduledLesson.startTime + scheduledLesson.duration, true),
                        scheduleProvider.getLessonInfoById(scheduledLesson.id).name,
                        scheduleProvider.getTeacherInfoById(scheduleProvider.getLessonInfoById(scheduledLesson.id).teacher).name));
                scheduledLessonText.setLayoutParams(params);
                binding.schedule.addView(scheduledLessonText);
                i1++;
            }

            TextView addScheduledLessonButton = new TextView(this);
            addScheduledLessonButton.setLayoutParams(params);
            addScheduledLessonButton.setGravity(Gravity.CENTER);
            addScheduledLessonButton.setText("{ДОБАВИТЬ}");
            addScheduledLessonButton.setTextSize(20f);
            addScheduledLessonButton.setBackgroundColor(Color.DKGRAY);

            addScheduledLessonButton.setOnClickListener(v -> {
                addScheduledLessonButton.setBackgroundColor(Color.BLUE);
                onAddScheduledLessonToWeekDay(finalWeekIndex, day);
            });
            binding.schedule.addView(addScheduledLessonButton);
            i++;
        }
    }


    public void onTeacherInfoClick(TeacherInfo teacherInfo) {
        LinearLayout dialogLinearLayout = new LinearLayout(this);
        EditText name = new EditText(this);
        Button deleteButton = new Button(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.teachers_title) + " - " + String.format("%s (%s)", teacherInfo.name, teacherInfo.id))
                .setView(dialogLinearLayout)
                .setPositiveButton(R.string.apply, (dialogInterface, i) -> {
                    teacherInfo.name = name.getText().toString();
                    scheduleProvider.save(ScheduleData.getScheduleFilePath(this));
                })
                .create();

        name.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        name.setHint("Введите имя учителя сюда");
        name.setText(teacherInfo.name);
        deleteButton.setText(R.string.delete);
        deleteButton.setOnClickListener(v -> {
            byte a = scheduleProvider.removeTeacherInfo(teacherInfo);
            if (a == 0) {
                scheduleProvider.save(ScheduleData.getScheduleFilePath(this));
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Ошибка! Сначала сделайте так что бы этот учитель не вёл ни какие уроки!", Toast.LENGTH_LONG).show();
            }
        });

        dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLinearLayout.addView(name);
        dialogLinearLayout.addView(deleteButton);

        dialog.show();
    }
    public void onLessonInfoClick(LessonInfo lessonInfo) {
        LinearLayout dialogLinearLayout = new LinearLayout(this);
        EditText name = new EditText(this);
        Spinner teacherSpinner = new Spinner(this);
        Button deleteButton = new Button(this);
        ArrayAdapter<TeacherInfo> teacherInfoArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, scheduleProvider.getTeacherInfoList());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.lessons_title) + " - " + String.format("%s (%s)", lessonInfo.name, lessonInfo.id))
                .setView(dialogLinearLayout)
                .setPositiveButton(R.string.apply, (dialogInterface, i) -> {
                    lessonInfo.name = name.getText().toString();
                    lessonInfo.teacher = teacherInfoArrayAdapter.getItem(teacherSpinner.getSelectedItemPosition()).id;
                    scheduleProvider.save(ScheduleData.getScheduleFilePath(this));
                })
                .create();

        name.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        name.setHint("Введите урока сюда");
        name.setText(lessonInfo.name);
        name.setTextSize(19f);
        teacherSpinner.setAdapter(teacherInfoArrayAdapter);
        teacherSpinner.setSelection(teacherInfoArrayAdapter.getPosition(scheduleProvider.getTeacherInfoById(lessonInfo.teacher)));
        deleteButton.setText(R.string.delete);
        deleteButton.setOnClickListener(v -> {
            byte a = scheduleProvider.removeLessonInfo(lessonInfo);
            if (a == 0) {
                scheduleProvider.save(ScheduleData.getScheduleFilePath(this));
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Ошибка! Сначала сделайте так что бы этот урок не был в расписании!", Toast.LENGTH_LONG).show();
            }
        });

        dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLinearLayout.addView(name);
        dialogLinearLayout.addView(teacherSpinner);
        dialogLinearLayout.addView(deleteButton);


        dialog.show();
    }

    public void onAddTeacherInfoClick() {
        LinearLayout dialogLinearLayout = new LinearLayout(this);
        EditText name = new EditText(this);

        name.setHint("Введите имя нового учителя сюда");
        name.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLinearLayout.addView(name);

        AlertDialog a = new AlertDialog.Builder(this)
                .setTitle("Добавление нового учителя")
                .setView(dialogLinearLayout)
                .setPositiveButton(R.string.apply, (dialogInterface, i) -> {
                    String teacherName = name.getText().toString();
                    if (teacherName.equals("")) return;
                    scheduleProvider.addTeacherInfo(teacherName);
                    scheduleProvider.save(ScheduleData.getScheduleFilePath(this));
                })
                .create();
        a.show();
    }
    public void onAddLessonInfoClick() {
        LinearLayout dialogLinearLayout = new LinearLayout(this);
        EditText nameEditText = new EditText(this);
        Spinner teacherSpinner = new Spinner(this);

        nameEditText.setHint("Введите имя нового урока сюда");
        nameEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ArrayAdapter<TeacherInfo> teacherInfoArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, scheduleProvider.getTeacherInfoList());
        teacherSpinner.setAdapter(teacherInfoArrayAdapter);


        dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLinearLayout.addView(nameEditText);
        dialogLinearLayout.addView(teacherSpinner);

        AlertDialog a = new AlertDialog.Builder(this)
                .setTitle("Добавление нового урока")
                .setView(dialogLinearLayout)
                .setPositiveButton(R.string.apply, (dialogInterface, i) -> {
                    String teacherName = nameEditText.getText().toString();
                    if (teacherName.equals("")) {
                        Toast.makeText(this, "Ошибка! Имя урока не может быть пустым", Toast.LENGTH_LONG).show();
                        return;
                    }
                    int selectedItemPosition = teacherSpinner.getSelectedItemPosition();
                    if (selectedItemPosition < 0) {
                        Toast.makeText(this, "Ошибка! Учитель не выбран!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    scheduleProvider.addLessonInfo(nameEditText.getText().toString(), teacherInfoArrayAdapter.getItem(selectedItemPosition).id);
                    scheduleProvider.save(ScheduleData.getScheduleFilePath(this));
                })
                .create();
        a.show();
    }

    public void onAddScheduledLessonToWeekDay(int weekDayPosition, List<ScheduledLesson> weekDay) {
        LinearLayout dialogLinearLayout = new LinearLayout(this);
        TextView lessonPositionTitle = new TextView(this);
        Spinner lessonPositionSpinet = new Spinner(this);
        Spinner lessonSpinner = new Spinner(this);
        EditText startTime = new EditText(this);
        EditText duration = new EditText(this);
        ArrayList<Short> positions = new ArrayList<>();

        short i = 0;
        for (ScheduledLesson ignored : weekDay) {
            i++;
            positions.add(i);
        }
        positions.add((short) (i+1));

        ArrayAdapter<Short> lessonPositionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, positions);
        ArrayAdapter<LessonInfo> teacherInfoArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, scheduleProvider.getLessonsInfoList());
        lessonPositionSpinet.setAdapter(lessonPositionAdapter);
        lessonPositionSpinet.setSelection(i);
        lessonSpinner.setAdapter(teacherInfoArrayAdapter);
        lessonPositionTitle.setText("Положение урока");
        startTime.setHint("Время начала часы:минуты");
        duration.setHint("Длительность часы:минуты");

        dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLinearLayout.addView(lessonPositionTitle);
        dialogLinearLayout.addView(lessonPositionSpinet);
        dialogLinearLayout.addView(lessonSpinner);
        dialogLinearLayout.addView(startTime);
        dialogLinearLayout.addView(duration);

        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
        AlertDialog a = new AlertDialog.Builder(this)
                .setTitle("Добавление нового урока на "+dateFormatSymbols.getWeekdays()[weekDayPosition])
                .setView(dialogLinearLayout)
                .setPositiveButton(R.string.apply, (ignored, ignored1) -> {
                    String[] star = startTime.getText().toString().split(":");
                    String[] dur = duration.getText().toString().split(":");

                    int _startTime;
                    int _duration;

                    try {
                        _startTime = (Short.parseShort(star[0]) * 60 * 60) + (Short.parseShort(star[1])*60);
                        _duration = (Short.parseShort(dur[0]) * 60 * 60) + (Short.parseShort(dur[1])*60);
                    } catch (Exception ignored2) {
                        Toast.makeText(this, "Ошибка времени", Toast.LENGTH_LONG).show();
                        return;
                    }
                    int selectedItemPosition = lessonPositionSpinet.getSelectedItemPosition();
                    if (selectedItemPosition < 0) {
                        Toast.makeText(this, "Ошибка! Положение урока не может быть пустым (чего???)!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    int selectedItemPositionLesson = lessonSpinner.getSelectedItemPosition();
                    if (selectedItemPositionLesson < 0) {
                        Toast.makeText(this, "Ошибка! Урок не выбран", Toast.LENGTH_LONG).show();
                        return;
                    }

                    weekDay.add(lessonPositionAdapter.getItem(selectedItemPosition)-1, new ScheduledLesson(teacherInfoArrayAdapter.getItem(selectedItemPositionLesson).id, _startTime, _duration));
                    scheduleProvider.save(ScheduleData.getScheduleFilePath(this));
                })
                .create();
        a.show();
    }

    public void onScheduledLessonClick(int weekDayPosition, List<ScheduledLesson> weekDay, ScheduledLesson scheduledLesson) {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
        LinearLayout dialogLinearLayout = new LinearLayout(this);
        TextView lessonPositionTitle = new TextView(this);
        Spinner lessonPositionSpinet = new Spinner(this);
        Spinner lessonSpinner = new Spinner(this);
        EditText startTime = new EditText(this);
        EditText duration = new EditText(this);
        ArrayList<Short> positions = new ArrayList<>();
        Button deleteButton = new Button(this);



        short pos = 0;
        short i = 0;
        for (ScheduledLesson ignored : weekDay) {
            i++;
            positions.add(i);
            if (ignored == scheduledLesson) pos = i;
        }
        //positions.add((short) (i+1));

        ArrayAdapter<Short> lessonPositionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, positions);
        ArrayAdapter<LessonInfo> teacherInfoArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, scheduleProvider.getLessonsInfoList());


        AlertDialog a = new AlertDialog.Builder(this)
                .setTitle("Редактирование урока нового урока на "+dateFormatSymbols.getWeekdays()[weekDayPosition])
                .setView(dialogLinearLayout)
                .setPositiveButton(R.string.apply, (ignored, ignored1) -> {
                    String[] star = startTime.getText().toString().split(":");
                    String[] dur = duration.getText().toString().split(":");

                    int _startTime;
                    int _duration;

                    try {
                        _startTime = (Short.parseShort(star[0]) * 60 * 60) + (Short.parseShort(star[1])*60);
                        _duration = (Short.parseShort(dur[0]) * 60 * 60) + (Short.parseShort(dur[1])*60);
                    } catch (Exception ignored2) {
                        Toast.makeText(this, "Ошибка времени", Toast.LENGTH_LONG).show();
                        return;
                    }

                    scheduledLesson.id = teacherInfoArrayAdapter.getItem(lessonSpinner.getSelectedItemPosition()).id;
                    scheduledLesson.duration = _duration;
                    scheduledLesson.startTime = _startTime;
                    weekDay.remove(scheduledLesson);
                    int ssda = lessonPositionAdapter.getItem(lessonPositionSpinet.getSelectedItemPosition())-1;
                    if (ssda < 0) ssda = 0;
                    weekDay.add(ssda, scheduledLesson);
                    scheduleProvider.save(ScheduleData.getScheduleFilePath(this));
                })
                .create();


        lessonPositionSpinet.setAdapter(lessonPositionAdapter);
        lessonPositionSpinet.setSelection(pos-1);
        lessonSpinner.setAdapter(teacherInfoArrayAdapter);
        lessonPositionTitle.setText("Положение урока");
        startTime.setHint("Время начала часы:минуты");
        duration.setHint("Длительность часы:минуты");
        lessonSpinner.setSelection(teacherInfoArrayAdapter.getPosition(scheduleProvider.getLessonInfoById(scheduledLesson.id)));
        deleteButton.setText(R.string.delete);
        deleteButton.setOnClickListener(v -> {
            weekDay.remove(scheduledLesson);
            a.dismiss();
        });

        startTime.setText(TimeUtil.secondsToDigitalTime(scheduledLesson.startTime, true, true));
        duration.setText(TimeUtil.secondsToDigitalTime(scheduledLesson.duration, true, true));

        dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLinearLayout.addView(lessonPositionTitle);
        dialogLinearLayout.addView(lessonPositionSpinet);
        dialogLinearLayout.addView(lessonSpinner);
        dialogLinearLayout.addView(startTime);
        dialogLinearLayout.addView(duration);
        dialogLinearLayout.addView(deleteButton);


        a.show();
    }
}