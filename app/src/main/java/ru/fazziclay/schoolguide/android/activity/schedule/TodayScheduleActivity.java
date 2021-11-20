package ru.fazziclay.schoolguide.android.activity.schedule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.data.schedule.Lesson;
import ru.fazziclay.schoolguide.databinding.ActivityTodayScheduleBinding;
import ru.fazziclay.schoolguide.util.TimeUtil;

public class TodayScheduleActivity extends AppCompatActivity {
    ActivityTodayScheduleBinding binding;
    SchoolGuide app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(false);
        }
        binding = ActivityTodayScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.openApp.setOnClickListener(ignore -> {
            Intent intent = new Intent(this, ScheduleEditActivity.class)
                    .putExtra(ScheduleEditActivity.KEY_LOCAL_SCHEDULE_UUID, app.getSettingsProvider().getSelectedLocalSchedule().toString());
            startActivity(intent);
            finish();
        });

        app = SchoolGuide.getInstance();

        BaseAdapter A = new BaseAdapter() {
            @Override
            public int getCount() {
                return app.getSelectedLocalSchedule().getToday().size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int childId, View convertView, ViewGroup parent) {
                int color = Color.GREEN;
                Lesson lesson = app.getSelectedLocalSchedule().getToday().get(childId);
                if (lesson.equals(app.getSelectedLocalSchedule().getNowLesson())) color = Color.CYAN;
                if (lesson.equals(app.getSelectedLocalSchedule().getNextLesson())) color = Color.RED;
                TextView textView = new TextView(TodayScheduleActivity.this);
                textView.setTextSize(21);
                textView.setTextColor(color);
                textView.setPadding(15, 1, 5, 1);
                textView.setText(String.format("#%s [%s - %s] %s",
                        childId + 1,
                        TimeUtil.secondsToHumanTime(lesson.getStart(), true).substring(0, 5),
                        TimeUtil.secondsToHumanTime(Math.min(lesson.getEnd(), 24 * 60 * 60 - 1), true).substring(0, 5),
                        app.getLessonDisplayName(lesson)));
                return textView;
            }
        };

        binding.todayLessons.setAdapter(A);
    }
}