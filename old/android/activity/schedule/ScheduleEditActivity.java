package ru.fazziclay.schoolguide.android.activity.schedule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.data.schedule.Lesson;
import ru.fazziclay.schoolguide.data.schedule.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.databinding.ActivityScheduleEditBinding;
import ru.fazziclay.schoolguide.util.TimeUtil;

public class ScheduleEditActivity extends AppCompatActivity {
    public static final String KEY_LOCAL_SCHEDULE_UUID = "localScheduleUUID";
    private static final boolean IS_MONDAY_FIRST = true;

    int[] weekDays = new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};
    String[] weekDaysNames = new DateFormatSymbols().getWeekdays();

    SchoolGuide app;

    SettingsProvider settingsProvider;
    ScheduleProvider scheduleProvider;
    ActivityScheduleEditBinding binding;

    UUID localScheduleUUID = null;
    LocalSchedule localSchedule = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            app = SchoolGuide.get(this);
            binding = ActivityScheduleEditBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());


            settingsProvider = app.getSettings();
            scheduleProvider = app.getSchedule();
            localScheduleUUID = UUID.fromString(getIntent().getExtras().getString(KEY_LOCAL_SCHEDULE_UUID));
            localSchedule = scheduleProvider.getLocalSchedule(localScheduleUUID);

            if (IS_MONDAY_FIRST) {
                weekDays = new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
            }

            initLayout();
        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initLayout();
        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
            finish();
        }
    }

    private void initLayout() {
        binding.deleteButton.setEnabled(!settingsProvider.isSyncDeveloperSchedule());
        binding.scheduleName.setEnabled(!settingsProvider.isSyncDeveloperSchedule());
        binding.scheduleName.setText(localSchedule.getName());
        binding.scheduleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (app.getSettings().isSyncDeveloperSchedule()) {
                    return;
                }

                localSchedule.setName(editable.toString());
                scheduleProvider.save();
            }
        });
        BaseExpandableListAdapter listAdapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return 7;
            }

            @Override
            public int getChildrenCount(int groupId) {
                if (localSchedule.get(weekDays[groupId]).size() == 0) return 1;
                return localSchedule.get(weekDays[groupId]).size();
            }

            @Override
            public Object getGroup(int groupId) {
                return null;
            }

            @Override
            public Object getChild(int groupId, int childId) {
                return null;
            }

            @Override
            public long getGroupId(int groupIndex) {
                return groupIndex;
            }

            @Override
            public long getChildId(int groupId, int childIndex) {
                return childIndex;
            }

            @Override
            public View getGroupView(int groupId, boolean isExpanded, View convertView, ViewGroup parent) {
                int weekValue = weekDays[groupId];
                String weekName = weekDaysNames[weekValue];
                Calendar calendar = new GregorianCalendar();
                if (calendar.get(Calendar.DAY_OF_WEEK) == weekValue) {
                    weekName += " <---";
                }

                TextView textView = new TextView(ScheduleEditActivity.this);
                textView.setTextColor(Color.CYAN);
                textView.setTextSize(30);
                textView.setText(weekName.toUpperCase());
                textView.setOnClickListener(ignore -> {
                            if (app.getSettings().isSyncDeveloperSchedule()) {
                                SchoolGuide.showWarnSyncDeveloperScheduleDialog(ScheduleEditActivity.this);
                                return;
                            }

                            startActivity(new Intent(ScheduleEditActivity.this, ScheduleLessonEditActivity.class)
                                    .putExtra(ScheduleLessonEditActivity.KEY_LOCAL_SCHEDULE_UUID, localScheduleUUID.toString())
                                    .putExtra(ScheduleLessonEditActivity.KEY_LOCAL_SCHEDULE_EDIT_DAY_OF_WEEK, weekValue));
                        }
                );

                convertView = textView;
                binding.lessonsList.expandGroup(groupId);
                binding.lessonsList.setGroupIndicator(null);
                return convertView;
            }

            @Override
            public View getChildView(int groupId, int childId, boolean b, View view, ViewGroup viewGroup) {
                if (localSchedule.get(weekDays[groupId]).size() == 0) {
                    TextView textView = new TextView(ScheduleEditActivity.this);
                    textView.setTextSize(21);
                    textView.setPadding(15, 1, 5, 1);
                    textView.setText(R.string.scheduleEdit_emptyDay);
                    textView.setTextColor(Color.LTGRAY);
                    return textView;
                }

                int colorNew = Color.GREEN;
                int colorD = Color.LTGRAY;

                int iii = groupId - 1;
                if (iii < 0) iii = weekDays.length - 1;
                int weekValuePr = weekDays[iii];
                List<Lesson> weekPr = localSchedule.get(weekValuePr);

                int weekValue = weekDays[groupId];
                List<Lesson> week = localSchedule.get(weekValue);
                Lesson lesson = week.get(childId);

                int color = colorNew;
                for (Lesson l : weekPr) {
                    if (l.getLessonInfo().equals(lesson.getLessonInfo())) {
                        color = colorD;
                        break;
                    }
                }

                if (lesson.equals(localSchedule.getNowLesson())) color = Color.CYAN;
                if (lesson.equals(localSchedule.getNextLesson())) color = Color.RED;

                TextView textView = new TextView(ScheduleEditActivity.this);
                textView.setTextSize(20);
                textView.setTextColor(color);
                textView.setPadding(12, 1, 5, 1);
                textView.setText(String.format("[%s. %s %s] %s",
                        childId + 1,
                        TimeUtil.secondsToHumanTime(lesson.getStart(), true).substring(0, 5),
                        TimeUtil.secondsToHumanTime(Math.min(lesson.getEnd(), 24 * 60 * 60 - 1), true).substring(0, 5),
                        getLessonText(lesson)));
                textView.setOnClickListener(ignore -> {
                    if (app.getSettings().isSyncDeveloperSchedule()) {
                        SchoolGuide.showWarnSyncDeveloperScheduleDialog(ScheduleEditActivity.this);
                        return;
                    }

                    startActivity(new Intent(ScheduleEditActivity.this, ScheduleLessonEditActivity.class)
                            .putExtra(ScheduleLessonEditActivity.KEY_LOCAL_SCHEDULE_UUID, localScheduleUUID.toString())
                            .putExtra(ScheduleLessonEditActivity.KEY_LOCAL_SCHEDULE_EDIT_DAY_OF_WEEK, weekValue)
                            .putExtra(ScheduleLessonEditActivity.KEY_LESSON_POSITION, childId)
                    );
                });
                return textView;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }
        };
        binding.lessonsList.setAdapter(listAdapter);

        binding.deleteButton.setEnabled(!settingsProvider.isSyncDeveloperSchedule());
        binding.deleteButton.setOnClickListener(ignore -> delete());
        binding.setButton.setVisibility(localScheduleUUID.equals(settingsProvider.getSelectedLocalSchedule()) ? View.GONE : View.VISIBLE);
        binding.setButton.setOnClickListener(ignore -> set());
    }

    private String getLessonText(Lesson lesson) {
        LessonInfo a = scheduleProvider.getLessonInfo(lesson.getLessonInfo());
        if (a == null) return getString(R.string.abc_unknown);
        return a.getName();
    }

    private void delete() {
        if (app.getSettings().isSyncDeveloperSchedule()) {
            SchoolGuide.showWarnSyncDeveloperScheduleDialog(this);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.scheduleEdit_delete_title)
                .setMessage(R.string.scheduleEdit_delete_message)
                .setPositiveButton(R.string.abc_delete, (ignore1, ignore2) -> {
                    scheduleProvider.removeLocalSchedule(localScheduleUUID);
                    UUID[] a = scheduleProvider.getAllSchedules();
                    if (a.length > 0) settingsProvider.setSelectedLocalSchedule(a[0]);
                    finish();
                })
                .setNegativeButton(R.string.abc_cancel, null);

        builder.show();
    }

    private void set() {
        settingsProvider.setSelectedLocalSchedule(localScheduleUUID);
        binding.setButton.setVisibility(View.GONE);
    }
}