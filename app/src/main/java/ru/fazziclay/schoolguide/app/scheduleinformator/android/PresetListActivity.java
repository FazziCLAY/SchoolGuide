package ru.fazziclay.schoolguide.app.scheduleinformator.android;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.multiplicationtrening.MultiplicationGameActivity;
import ru.fazziclay.schoolguide.app.scheduleinformator.AppSchedule;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.databinding.ActivityPresetListBinding;

public class PresetListActivity extends AppCompatActivity {
    SchoolGuideApp app;
    ScheduleInformatorApp informatorApp;
    ActivityPresetListBinding binding;

    AppSchedule appSchedule;

    UUID[] presets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        informatorApp = app.getScheduleInformatorApp();
        appSchedule = informatorApp.getAppSchedule();

        binding = ActivityPresetListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.game.setOnClickListener(ignore -> MultiplicationGameActivity.open(this, false));

        updateList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void updateList() {
        presets = appSchedule.getPresetsUUIDs();
        binding.presetList.deferNotifyDataSetChanged();
        binding.presetList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return presets.length;
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
                UUID uuid = presets[position];
                Preset preset = appSchedule.getPreset(uuid);

                LinearLayout layout = new LinearLayout(PresetListActivity.this);
                layout.setOrientation(LinearLayout.HORIZONTAL);

                CheckBox checkBox = new CheckBox(PresetListActivity.this);
                checkBox.setChecked(preset.equals(appSchedule.getSelectedPreset()));
                checkBox.setTextSize(30);
                checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                checkBox.setOnClickListener(view -> {
                    appSchedule.setSelectedPreset(preset);
                    informatorApp.saveAppSchedule();
                    updateList();
                });

                TextView textView = new TextView(PresetListActivity.this);
                textView.setText(preset != null ? preset.name : "null");
                textView.setTextSize(30);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                textView.setOnClickListener(view -> startActivity(PresetEditActivity.getLaunchIntent(PresetListActivity.this, uuid)));

                layout.addView(checkBox);
                layout.addView(textView);

                return layout;
            }
        });
    }
}