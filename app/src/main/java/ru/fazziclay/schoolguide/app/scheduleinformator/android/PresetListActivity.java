package ru.fazziclay.schoolguide.app.scheduleinformator.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

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
    private SchoolGuideApp app;
    private ScheduleInformatorApp informatorApp;
    private ActivityPresetListBinding binding;

    private Schedule appSchedule;

    private UUID[] listPresetsUUIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        informatorApp = app.getScheduleInformatorApp();
        appSchedule = informatorApp.getSchedule();

        binding = ActivityPresetListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addPreset.setOnClickListener(ignore -> showCreateNewPresetDialog());

        registerForContextMenu(binding.presetList);

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
        menu.findItem(R.id.openDebugItem).setVisible(app.getSettings().developerFeatures);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.openUpdateCenterItem) {
            startActivity(UpdateCenterActivity.getLaunchIntent(this));

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
        name.setHint(R.string.presetList_createNew_nameHint);
        name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.presetList_createNew_title)
                .setView(layout)
                .setPositiveButton(R.string.presetList_createNew_create, (e, e1) -> {
                    appSchedule.putPreset(UUIDUtil.generateUUID(appSchedule.getPresetsUUIDs()), new Preset(name.getText().toString()));
                    informatorApp.saveAppSchedule();
                    updateList();
                })
                .setNegativeButton(R.string.presetList_createNew_cancel, null)
                .create();

        dialog.show();
    }

    private void updateList() {
        listPresetsUUIDs = appSchedule.getPresetsUUIDs();
        binding.presetList.deferNotifyDataSetChanged();
        binding.presetList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return listPresetsUUIDs.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @SuppressLint("ViewHolder")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                UUID presetUUID = listPresetsUUIDs[position];
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
                PopupMenu popupMenu = new PopupMenu(PresetListActivity.this, textView);
                popupMenu.inflate(R.menu.menu_preset);
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.copy) {
                        Gson gson = new Gson();
                        Preset g = gson.fromJson(gson.toJson(preset, Preset.class), Preset.class);
                        appSchedule.putPreset(UUIDUtil.generateUUID(appSchedule.getPresetsUUIDs()), g);
                        updateList();
                    } else if (item.getItemId() == R.id.delete) {
                        appSchedule.removePreset(presetUUID);
                        updateList();
                    }
                    informatorApp.saveAppSchedule();
                    return true;
                });
                textView.setText(preset == null ? "(null)" : preset.getName());
                textView.setTextSize(30);
                textView.setTextColor(Color.WHITE);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                textView.setOnClickListener(view -> startActivity(PresetEditActivity.getLaunchIntent(PresetListActivity.this, presetUUID)));
                textView.setOnLongClickListener(v -> {
                    popupMenu.show();
                    return true;
                });

                layout.addView(checkBox);
                layout.addView(textView);

                return layout;
            }
        });
    }
}