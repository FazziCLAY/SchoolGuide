package ru.fazziclay.schoolguide.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.Locale;

import ru.fazziclay.schoolguide.Clock;
import ru.fazziclay.schoolguide.data.SchoolDay;
import ru.fazziclay.schoolguide.data.SchoolLesson;
import ru.fazziclay.schoolguide.data.SchoolWeek;
import ru.fazziclay.schoolguide.data.Settings;
import ru.fazziclay.schoolguide.databinding.ActivityHomeBinding;
import ru.fazziclay.schoolguide.data.jsonparser.JsonRoot;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Debug Check Box
        //CheckBox debug = new CheckBox(this);
        //debug.setText(("Debug"));
        //debug.setChecked(ForegroundService.DEBUG_NOTIFY);
        //debug.setOnClickListener(view -> ForegroundService.DEBUG_NOTIFY = debug.isChecked());
        //binding.root.addView(debug);

        Button button = new Button(this);
        button.setText("Настройки");
        button.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
        binding.root.addView(button);


        // Schedule Text
        TextView textView = new TextView(this);
        textView.setText(String.format("Версия - %s\nЧто бы расписание появилось в программе нужно отредакрировать или перенести файл с расписанием в %s", 1, JsonRoot.getSchoolFilePath(this)));
        binding.root.addView(textView);

        StringBuilder text = new StringBuilder();
        int weekIteration = 1;
        int localLessonNumber = 1;
        for (SchoolDay a : SchoolWeek.getSchoolWeek().getList()) {
            int index = weekIteration+1;
            if (index == 8) index = 1;
            text.append(String.format("\n====== [%s] %s ======\n", weekIteration, dateFormatSymbols.getWeekdays()[index].toUpperCase()));
            for (SchoolLesson lesson : a.getLessons()) {
                text.append(String.format("[%s] %s - %s %s(%s)\n",
                        localLessonNumber,
                        Clock.millisToString(lesson.getStartTime(), true),
                        Clock.millisToString(lesson.getEndTime(), true),
                        lesson.getName(),
                        lesson.getTeacher()));
                localLessonNumber++;
            }
            localLessonNumber = 1;
            weekIteration++;
        }

        TextView scheduleText = new TextView(this);
        scheduleText.setTextSize(15f);
        scheduleText.setText(text.toString());
        binding.schoolRaspisanie.addView(scheduleText);
    }
}