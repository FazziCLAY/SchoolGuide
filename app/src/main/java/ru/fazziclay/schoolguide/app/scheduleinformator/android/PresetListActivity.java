package ru.fazziclay.schoolguide.app.scheduleinformator.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.UpdateCenterActivity;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.multiplicationtrening.MathTreningGameActivity;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Schedule;
import ru.fazziclay.schoolguide.databinding.ActivityPresetListBinding;
import ru.fazziclay.schoolguide.util.UUIDUtil;

public class PresetListActivity extends AppCompatActivity {
    SchoolGuideApp app;
    ScheduleInformatorApp informatorApp;
    ActivityPresetListBinding binding;

    Schedule appSchedule;

    UUID[] presets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        informatorApp = app.getScheduleInformatorApp();
        appSchedule = informatorApp.getSchedule();

        binding = ActivityPresetListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.game.setOnClickListener(ignore -> {
            //MultiplicationGameActivity.open(this, false);
            startActivity(new Intent(this, UpdateCenterActivity.class));
        });
        binding.createPreset.setOnClickListener(ignore -> showCreateNewPresetDialog());

        updateList();
    }

    @Override
    protected void onResume() {
        super.onResume(); // TODO: 2022-01-21 make translatable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = getSystemService(PowerManager.class);
            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                @SuppressLint("BatteryLife")
                AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setTitle("Оптимизация батареи")
                        .setMessage("Просим вас отключить оптимизацию батареи для прилжения, что бы оно корректно работало")
                        .setPositiveButton("Отключить", (dialog1, which) -> {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("Отмена", null);

                dialog.show();
            }
        }
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.openUpdateCenterItem) {
            UpdateCenterActivity.open(this);

        } else if (item.getItemId() == R.id.openMathTreningGameItem) {
            MathTreningGameActivity.open(this);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showCreateNewPresetDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText name = new EditText(this);
        name.setHint("Введите название");
        name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Создание нового пресета")
                .setView(layout)
                .setPositiveButton("Создать", (e, e1) -> {
                    appSchedule.putPreset(UUIDUtil.generateUUID(appSchedule.getPresetsUUIDs()), new Preset(name.getText().toString()));
                    informatorApp.saveAppSchedule();
                    updateList();
                })
                .create(); // TODO: 2022-01-20 make translatable

        dialog.show();
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
                UUID presetUUID = presets[position];
                Preset preset = appSchedule.getPreset(presetUUID);

                LinearLayout layout = new LinearLayout(PresetListActivity.this);
                layout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams checkboxLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                CheckBox checkBox = new CheckBox(PresetListActivity.this);
                checkBox.setChecked(preset.equals(informatorApp.getCurrentPreset()));
                checkBox.setTextSize(30);
                checkBox.setPadding(5, 5, 10, 5);
                checkBox.setLayoutParams(checkboxLayoutParams);
                checkBox.setOnClickListener(view -> {
                    view.clearAnimation();
                    informatorApp.setCurrentPreset(presetUUID);
                    informatorApp.saveAppSchedule();
                    updateList();
                });

                TextView textView = new TextView(PresetListActivity.this);
                textView.setText(preset == null ? "(null)" : preset.getName());
                textView.setTextSize(30);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                textView.setOnClickListener(view -> startActivity(PresetEditActivity.getLaunchIntent(PresetListActivity.this, presetUUID)));

                layout.addView(checkBox);
                layout.addView(textView);

                return layout;
            }
        });
    }
}