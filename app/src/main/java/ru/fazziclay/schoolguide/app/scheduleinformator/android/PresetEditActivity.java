package ru.fazziclay.schoolguide.app.scheduleinformator.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.CompressedEvent;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.databinding.ActivityPresetEditBinding;
import ru.fazziclay.schoolguide.util.time.ConvertMode;
import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class PresetEditActivity extends AppCompatActivity {
    private static final String EXTRA_PRESET_UUID = "uuid";

    public static Intent getLaunchIntent(Context context, UUID uuid) {
        Intent intent = new Intent(context, PresetEditActivity.class);
        intent.putExtra(EXTRA_PRESET_UUID, uuid.toString());

        return intent;
    }

    boolean firstMonday = true;

    SchoolGuideApp app;
    ScheduleInformatorApp informatorApp;
    ActivityPresetEditBinding binding;

    UUID presetUUID;
    Preset preset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        informatorApp = app.getScheduleInformatorApp();
        binding = ActivityPresetEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presetUUID = UUID.fromString(getIntent().getExtras().getString(EXTRA_PRESET_UUID));
        preset = informatorApp.getAppSchedule().getPreset(presetUUID);

        binding.presetName.setText(preset.name);

        updateEventList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEventList();
    }

    private void updateEventList() {
        binding.eventList.deferNotifyDataSetChanged();
        binding.eventList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return preset.events.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                position = getWeekByPosition(position);
                CompressedEvent event = preset.eventCompress(preset.events.get(position));


                TextView textView = new TextView(PresetEditActivity.this);
                textView.setTextSize(20);
                String start = TimeUtil.convertToHumanTime(event.getStart(), ConvertMode.hhMMSS);
                String end = TimeUtil.convertToHumanTime(event.getEnd(), ConvertMode.hhMMSS);
                textView.setText(String.format("%s [%s-%s] %s", position+1, start, end, event.getName()));
                return textView;
            }
        });
    }

    private int getWeekByPosition(int pos) {
        if (firstMonday) {
            return pos;
        }
        return pos;
    }
}