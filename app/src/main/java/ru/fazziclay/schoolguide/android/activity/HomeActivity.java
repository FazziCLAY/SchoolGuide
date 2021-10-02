package ru.fazziclay.schoolguide.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import ru.fazziclay.fazziclaylibs.TimeUtil;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.cache.StateCache;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.data.schedule.ScheduledLesson;
import ru.fazziclay.schoolguide.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
    Button settingsButton = null;
    Button scheduleConfiguratorButton = null;
    Button earlyFinishButton = null;
    Button restorePointsButton = null;

    public void setEarlyFinishButton() {
        if (ForegroundService.getInstance().isEarlyFinished()) {
            earlyFinishButton.setText("( ОТМЕНИТЬ ДОСРОЧНОЕ ЗАВЕРШЕНИЕ )");
            earlyFinishButton.setOnClickListener(v -> {
                ForegroundService.getInstance().getStateCache().earlyFinishedForDay = StateCache.EARLY_FINISHED_FOR_DAY_NOT_SET;
                ForegroundService.getInstance().setEarlyFinished(false);
                ForegroundService.getInstance().syncCache();

                setEarlyFinishButton();
            });
        } else {
            earlyFinishButton.setText("!! Досрочно завершить !!");
            earlyFinishButton.setOnClickListener(v -> {
                ForegroundService.getInstance().getStateCache().earlyFinishedForDay = (short) new GregorianCalendar().get(Calendar.DAY_OF_YEAR);
                ForegroundService.getInstance().setEarlyFinished(true);
                ForegroundService.getInstance().syncCache();

                setEarlyFinishButton();
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsButton = new Button(this);
        earlyFinishButton = new Button(this);
        scheduleConfiguratorButton = new Button(this);
        restorePointsButton = new Button(this);

        settingsButton.setText(R.string.settings);
        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        scheduleConfiguratorButton.setText(R.string.schedule_configurator);
        scheduleConfiguratorButton.setOnClickListener(v -> startActivity(new Intent(this, ScheduleConfiguratorActivity.class)));

        restorePointsButton.setText(R.string.restore_points);
        restorePointsButton.setOnClickListener(v -> startActivity(new Intent(this, RestorePointActivity.class)));

        StringBuilder text = new StringBuilder();
        ScheduleProvider scheduleProvider = ForegroundService.getInstance().getScheduleProvider();
        List<ScheduledLesson> list = scheduleProvider.getTodayLessons();
        if (!list.isEmpty()) {
            text.append("Уроки на сегодня такие:");
            int i = 0;
            while (i < list.size()) {
                ScheduledLesson scheduledLesson = list.get(i);
                text.append(String.format("\n[%s] [%s - %s] %s (%s)",
                        i+1,
                        TimeUtil.secondsToDigitalTime(scheduledLesson.startTime, true),
                        TimeUtil.secondsToDigitalTime(scheduledLesson.startTime + scheduledLesson.duration, true),
                        scheduleProvider.getLessonInfoById(scheduledLesson.id).name,
                        scheduleProvider.getTeacherInfoById(scheduleProvider.getLessonInfoById(scheduledLesson.id).teacher).name));
                i++;
            }

        } else {
            text.append("Сегодня уроков не обнаружено!");
        }

        TextView scheduleText = new TextView(this);
        scheduleText.setTextSize(15f);
        scheduleText.setText(text.toString());

        binding.root.addView(earlyFinishButton);
        binding.root.addView(settingsButton);
        binding.root.addView(scheduleConfiguratorButton);
        binding.root.addView(restorePointsButton);
        binding.schoolRaspisanie.addView(scheduleText);

        setEarlyFinishButton();
    }
}