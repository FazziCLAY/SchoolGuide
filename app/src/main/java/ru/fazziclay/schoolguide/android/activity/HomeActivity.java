package ru.fazziclay.schoolguide.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.Locale;

import ru.fazziclay.schoolguide.Clock;
import ru.fazziclay.schoolguide.SchoolDay;
import ru.fazziclay.schoolguide.SchoolLesson;
import ru.fazziclay.schoolguide.SchoolWeek;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Debug Check Box
        CheckBox debug = new CheckBox(this);
        debug.setText(("Debug"));
        debug.setChecked(ForegroundService.DEBUG_NOTIFY);
        debug.setOnClickListener(view -> ForegroundService.DEBUG_NOTIFY = debug.isChecked());
        binding.root.addView(debug);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Button button = new Button(this);
        button.setOnClickListener(v -> {
            vibrator.vibrate(100);
        });

        binding.root.addView(button);

        // Schedule Text
        StringBuilder text = new StringBuilder();
        int weekIteration = 1;
        int localLessonNumber = 1;
        for (SchoolDay a : SchoolWeek.getSchoolWeek().getList()) {
            text.append(String.format("\n====== [%s] %s ======\n", weekIteration, dateFormatSymbols.getWeekdays()[weekIteration+1]));
            for (SchoolLesson lesson : a.getLessons()) {
                text.append(String.format("[%s] %s - %s %s(%s)\n",
                        localLessonNumber,
                        Clock.millisToString(lesson.getStartTime()),
                        Clock.millisToString(lesson.getEndTime()),
                        lesson.getName(),
                        lesson.getTeacher()));
                localLessonNumber++;
            }
            localLessonNumber = 1;
            weekIteration++;
        }

        TextView scheduleText = new TextView(this);
        scheduleText.setTextSize(13f);
        scheduleText.setText(text.toString());
        binding.schoolRaspisanie.addView(scheduleText);
    }
}