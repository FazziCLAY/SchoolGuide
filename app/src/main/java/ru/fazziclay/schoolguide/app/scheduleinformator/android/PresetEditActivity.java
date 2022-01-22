package ru.fazziclay.schoolguide.app.scheduleinformator.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.CompressedEvent;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Event;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.databinding.ActivityPresetEditBinding;
import ru.fazziclay.schoolguide.util.time.ConvertMode;
import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class PresetEditActivity extends AppCompatActivity {
    private static final int SECONDS_IN_DAY = 24 * 60 * 60;
    private static final String EXTRA_PRESET_UUID = "uuid";

    public static Intent getLaunchIntent(Context context, UUID uuid) {
        Intent intent = new Intent(context, PresetEditActivity.class);
        intent.putExtra(EXTRA_PRESET_UUID, uuid.toString());

        return intent;
    }

    SchoolGuideApp app;
    ScheduleInformatorApp informatorApp;
    DateFormatSymbols dateFormatSymbols;
    ActivityPresetEditBinding binding;

    UUID presetUUID;
    Preset preset;
    boolean isFirstMonday = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        informatorApp = app.getScheduleInformatorApp();
        dateFormatSymbols = new DateFormatSymbols();

        presetUUID = UUID.fromString(getIntent().getExtras().getString(EXTRA_PRESET_UUID));
        preset = informatorApp.getSchedule().getPreset(presetUUID);

        binding = ActivityPresetEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(getString(R.string.presetEdit_activityTitle, preset.getName()));

        updateEventList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEventList();
    }

    /**
     * @param week неделя в формате {@link Calendar#SUNDAY}
     * **/
    private Event[] getEventsInWeek(int week) {
        List<Event> e = new ArrayList<>();
        week--;

        for (Event event : preset.eventsPositions) {
            int w = (int) Math.floor(event.getStart() / (double) (24 * 60 * 60));
            Log.d("getEventsInWeek", "eventName="+preset.eventsInfos.get(event.getEventInfo()).getName()+"; w="+w);
            if (week == w) e.add(event);
        }
        return e.toArray(new Event[0]);
    }

    private void updateEventList() {
        binding.eventList.deferNotifyDataSetChanged();
        binding.eventList.setAdapter(new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return 7;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return getEventsInWeek(posToWeek(isFirstMonday, groupPosition)).length;
            }

            @Override
            public Object getGroup(int groupPosition) {
                return null;
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return null;
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                binding.eventList.expandGroup(groupPosition, false);
                TextView textView = new TextView(PresetEditActivity.this);
                textView.setTextSize(30);
                textView.setText(dateFormatSymbols.getWeekdays()[posToWeek(isFirstMonday, groupPosition)]);
                return textView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                int timeDeleteCoff = SECONDS_IN_DAY * (groupPosition + 1);
                Event e = getEventsInWeek(posToWeek(isFirstMonday, groupPosition))[childPosition];
                if (e == null) return new CheckBox(PresetEditActivity.this);
                CompressedEvent event = preset.compressEvent(e);

                TextView textView = new TextView(PresetEditActivity.this);
                textView.setTextSize(20);
                String start = TimeUtil.convertToHumanTime(event.getStart() - timeDeleteCoff, ConvertMode.HHMM);
                String end = TimeUtil.convertToHumanTime(event.getEnd() - timeDeleteCoff, ConvertMode.HHMM);
                textView.setText(String.format("%s %s-%s %s", childPosition+1, start, end, event.getName()));
                return textView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        });
        binding.eventList.setGroupIndicator(null);
    }

    public static int posToWeek(boolean firstMonday, int pos) {
        pos++;
        if (firstMonday) {
            pos++;
            if (pos > 7) pos = Calendar.SUNDAY;
        }
        return pos;
    }
}