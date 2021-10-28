package ru.fazziclay.schoolguide.android.activity.schedule;

import android.content.Context;
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
import java.util.UUID;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.schedule.Lesson;
import ru.fazziclay.schoolguide.data.schedule.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.databinding.ActivityScheduleEditBinding;
import ru.fazziclay.schoolguide.util.TimeUtil;

public class ScheduleEditActivity extends AppCompatActivity {
    public static final String KEY_LOCAL_SCHEDULE_UUID = "localScheduleUUID";
    private static final boolean IS_MONDAY_FIRST = true;

    CrashReport crashReport;
    int[] weekDays = new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};
    String[] weekDaysNames = new DateFormatSymbols().getWeekdays();

    ScheduleProvider scheduleProvider;
    ActivityScheduleEditBinding binding;

    UUID localScheduleUUID = null;
    LocalSchedule localSchedule = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crashReport = new CrashReport(CrashReport.getFolder(this));
        try {
            binding = ActivityScheduleEditBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            scheduleProvider = ForegroundService.getInstance().getScheduleProvider();
            localScheduleUUID = UUID.fromString(getIntent().getExtras().getString(KEY_LOCAL_SCHEDULE_UUID));
            localSchedule = scheduleProvider.getLocalSchedule(localScheduleUUID);

            if (IS_MONDAY_FIRST) {
                weekDays = new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
            }

            initLayout();
        } catch (Throwable throwable) {
            crashReport.error(throwable);
            crashReport.notifyUser(this);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLayout();
    }

    private void initLayout() {
        binding.scheduleName.setText(localSchedule.getName());
        binding.scheduleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                localSchedule.setName(editable.toString());
                scheduleProvider.save();
            }
        });

        Context context = this;
        BaseExpandableListAdapter listAdapter = new BaseExpandableListAdapter() {

            @Override
            public int getGroupCount() {
                return 7;
            }

            @Override
            public int getChildrenCount(int groupId) {
                return localSchedule.get(weekDays[groupId]).size();
            }

            @Override
            public Object getGroup(int groupId) {
                return null;
            }

            @Override
            public Object getChild(int groupId, int childId) {
                return "Child - groupId:"+groupId+" - childId:"+childId;
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

                if (convertView == null) {
                    TextView textView = new TextView(context);
                    textView.setTextSize(30);
                    textView.setText(weekName.toUpperCase());
                    textView.setOnClickListener(ignore -> startActivity(new Intent(context, ScheduleLessonEditActivity.class)
                            .putExtra(ScheduleLessonEditActivity.KEY_LOCAL_SCHEDULE_UUID, localScheduleUUID.toString())
                            .putExtra(ScheduleLessonEditActivity.KEY_LOCAL_SCHEDULE_EDIT_DAY_OF_WEEK, weekValue))
                );

                    convertView = textView;
                }
                binding.lessonsList.expandGroup(groupId);
                return convertView;
            }

            @Override
            public View getChildView(int groupId, int childId, boolean b, View view, ViewGroup viewGroup) {
                int weekValue = weekDays[groupId];
                Lesson lesson = localSchedule.get(weekValue).get(childId);

                TextView textView = new TextView(context);
                textView.setTextSize(21);
                textView.setTextColor(Color.GREEN);
                textView.setText(String.format("[%s - %s] %s",
                        TimeUtil.secondsToHumanTime(lesson.getStart(), true),
                        TimeUtil.secondsToHumanTime(Math.min(lesson.getEnd(), 24 * 60 * 60-1), true),
                        getLessonText(lesson)));
                textView.setOnClickListener(ignore -> startActivity(new Intent(context, ScheduleLessonEditActivity.class)
                        .putExtra(ScheduleLessonEditActivity.KEY_LOCAL_SCHEDULE_UUID, localScheduleUUID.toString())
                        .putExtra(ScheduleLessonEditActivity.KEY_LOCAL_SCHEDULE_EDIT_DAY_OF_WEEK, weekValue)
                        .putExtra(ScheduleLessonEditActivity.KEY_LESSON_POSITION, childId)
                ));
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

        binding.deleteButton.setOnClickListener(ignore -> delete());
    }

    private String getLessonText(Lesson lesson) {
        LessonInfo a = scheduleProvider.getLessonInfo(lesson.getLessonInfo());
        if (a == null) return "Unknown";
        return a.getName();
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.scheduleEdit_delete_title)
                .setMessage(R.string.scheduleEdit_delete_message)
                .setPositiveButton(R.string.abc_delete, (ignore1, ignore2) -> {
                    scheduleProvider.removeLocalSchedule(localScheduleUUID);
                    finish();
                })
                .setNegativeButton(R.string.abc_cancel, null);

        builder.show();
    }
}