package ru.fazziclay.schoolguide.app.scheduleinformator.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import ru.fazziclay.schoolguide.DebugActivity;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SettingsActivity;
import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.UpdateCenterActivity;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.multiplicationtrening.MathTreningGameActivity;
import ru.fazziclay.schoolguide.app.scheduleinformator.AppPresetList;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.PresetList;
import ru.fazziclay.schoolguide.callback.CallbackImportance;
import ru.fazziclay.schoolguide.callback.Status;
import ru.fazziclay.schoolguide.databinding.ActivityPresetListBinding;

public class PresetListActivity extends AppCompatActivity {
    public static final int PRESET_NAME_MAX_LENGTH = 25;
    public static final int PRESET_NAME_MAX_LINES = 1;

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, PresetListActivity.class);
    }

    private SchoolGuideApp app;
    private ScheduleInformatorApp informatorApp;
    private ActivityPresetListBinding binding;
    private MenuItem openUpdateCenterMenuItem;

    private PresetList presetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        if (app == null) {
            setContentView(SharedConstrains.getAppNullView(this));
            return;
        }
        informatorApp = app.getScheduleInformatorApp();
        presetList = informatorApp.getSchedule();

        app.getGlobalUpdateCallbacks().addCallback(CallbackImportance.DEFAULT, (exception, globalKeys, globalVersionManifest, globalBuiltinPresetList) -> {
            if (isFinishing()) return new Status.Builder()
                    .setDeleteCallback(true)
                    .build();

            runOnUiThread(this::updateOpenUpdateCenterMenuName);

            return new Status.Builder()
                    .setDeleteCallback(false)
                    .build();
        });

        binding = ActivityPresetListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addPreset.setOnClickListener(ignore -> showCreateNewPresetDialog());

        updateList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showDisableBatteryOptimizationDialog();
        }
        updateOpenUpdateCenterMenuName();
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.openDebugItem).setVisible(app.getSettings().developerFeatures);
        openUpdateCenterMenuItem = menu.findItem(R.id.openUpdateCenterItem);
        updateOpenUpdateCenterMenuName();
        return true;
    }

    private void updateOpenUpdateCenterMenuName() {
        if (openUpdateCenterMenuItem == null) return;
        if (app.isUpdateAvailable()) {
            openUpdateCenterMenuItem.setTitle(String.format("(!) %s", getString(R.string.mainOptionMenu_openUpdateCenter)));
        } else {
            openUpdateCenterMenuItem.setTitle(R.string.mainOptionMenu_openUpdateCenter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.openUpdateCenterItem) {
            startActivity(UpdateCenterActivity.getLaunchIntent(this));

        } else if (id == R.id.openMathTreningGameItem) {
            startActivity(MathTreningGameActivity.getLaunchIntent(this));

        } else if (id == R.id.openSettingsItem) {
            startActivity(SettingsActivity.getLaunchIntent(this));

        } else if (id == R.id.openDebugItem) {
            startActivity(DebugActivity.getLaunchIntent(this));
        }
        return true;
    }

    /**
     * Показывает диолог и том что было бы неплохо отключить
     * оптимизацию батареи для приложения, диолог показывается только если это ещё не сделано
     * **/
    @SuppressLint("BatteryLife")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showDisableBatteryOptimizationDialog() {
        PowerManager powerManager = getSystemService(PowerManager.class);
        if (powerManager.isIgnoringBatteryOptimizations(getPackageName())) return;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.batteryOptimizationDialog_title)
                .setMessage(R.string.batteryOptimizationDialog_message)
                .setPositiveButton(R.string.batteryOptimizationDialog_disable, (dialogInterface, which) -> {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        /* don`t puke .__. */
                    }
                })
                .setNegativeButton(R.string.batteryOptimizationDialog_cancel, null);

        dialog.show();
    }

    /**
     * Показать диолог создания нового пресета.
     * @see PresetListActivity#PRESET_NAME_MAX_LINES
     * @see PresetListActivity#PRESET_NAME_MAX_LENGTH
     * **/
    private void showCreateNewPresetDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText name = new EditText(this);
        name.setPadding(13, 10, 13, 10);
        name.setHint(R.string.presetList_createNew_nameHint);
        name.setMaxLines(PRESET_NAME_MAX_LINES);
        name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PRESET_NAME_MAX_LENGTH)});
        name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.presetList_createNew_title)
                .setView(layout)
                .setPositiveButton(R.string.presetList_createNew_create, (dialogInterface, which) -> {
                    String newName = name.getText().toString();
                    if (newName.isEmpty()) {
                        Toast.makeText(this, R.string.presetList_presetNameIsEmptyError, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    presetList.addPreset(new Preset(newName));
                    informatorApp.saveAppSchedule();
                    updateList();
                })
                .setNegativeButton(R.string.presetList_createNew_cancel, null)
                .create();

        dialog.show();
    }

    /**
     * Показать диолог удаления пресета
     * Если в {@link PresetListActivity#presetList} нету выбранного пресета, то не показываем
     * @param uuid нужный пресет
     * **/
    private void showDeletePresetDialog(UUID uuid) {
        Preset preset = presetList.getPreset(uuid);
        if (preset == null) {
            Log.e("showDeletePresetDialog", "preset null in schedule; uuid="+uuid.toString());
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.presetList_delete_title, preset.getName()))
                .setMessage(getString(R.string.presetList_delete_message))
                .setPositiveButton(R.string.presetList_delete, (dialogInterface, which) -> {
                    if (presetList instanceof AppPresetList) {
                        boolean selected = ((AppPresetList) presetList).getSelectedPreset() == preset;
                        presetList.removePreset(uuid);
                        if (selected) ((AppPresetList) presetList).selectFirst();
                    } else {
                        presetList.removePreset(uuid);
                    }
                    informatorApp.saveAppSchedule();
                    updateList();
                })
                .setNegativeButton(R.string.presetList_delete_cancel, null)
                .create();

        dialog.show();
    }

    /**
     * Показать диолог копирования пресета
     * @param uuid нужынй пресет
     * **/
    private void showCopyPresetDialog(UUID uuid) {
        Preset preset = presetList.getPreset(uuid);
        if (preset == null) {
            Log.e("showCopyPresetDialog", "preset null in schedule; uuid="+uuid.toString());
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText name = new EditText(this);
        name.setPadding(13, 10, 13, 10);
        name.setHint(R.string.presetList_copy_nameHint);
        name.setText(getString(R.string.presetList_copy_copyName, preset.getName()));
        name.setMaxLines(PRESET_NAME_MAX_LINES);
        name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PRESET_NAME_MAX_LENGTH)});
        name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.presetList_copy_title, preset.getName()))
                .setMessage(getString(R.string.presetList_copy_message))
                .setView(layout)
                .setPositiveButton(R.string.presetList_copy, (e, e1) -> {
                    String newName = name.getText().toString();
                    if (newName.isEmpty()) {
                        Toast.makeText(this, R.string.presetList_presetNameIsEmptyError, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Preset newPreset = preset.clone();
                    newPreset.setName(newName);
                    presetList.addPreset(newPreset);
                    informatorApp.saveAppSchedule();
                    updateList();
                })
                .setNegativeButton(R.string.presetList_copy_cancel, null)
                .create();

        dialog.show();
    }

    /**
     * Показать диолог копирования пресета
     * @param uuid нужынй пресет
     * **/
    private void showRenamePresetDialog(UUID uuid) {
        Preset preset = presetList.getPreset(uuid);
        if (preset == null) {
            Log.e("showRenamePresetDialog", "preset null in schedule; uuid="+uuid.toString());
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText name = new EditText(this);
        name.setPadding(13, 10, 13, 10);
        name.setHint(R.string.presetList_rename_nameHint);
        name.setText(preset.getName());
        name.setMaxLines(PRESET_NAME_MAX_LINES);
        name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PRESET_NAME_MAX_LENGTH)});
        name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.presetList_rename_title, preset.getName()))
                .setMessage(getString(R.string.presetList_rename_message))
                .setView(layout)
                .setPositiveButton(R.string.presetList_rename, (e, e1) -> {
                    String newName = name.getText().toString();
                    if (newName.isEmpty()) {
                        Toast.makeText(this, R.string.presetList_presetNameIsEmptyError, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    preset.setName(newName);
                    informatorApp.saveAppSchedule();
                    updateList();
                })
                .setNegativeButton(R.string.presetList_rename_cancel, null)
                .create();

        dialog.show();
    }

    /**
     * Обновить View список
     * **/
    private void updateList() {
        UUID[] listPresetsUUIDs = presetList.getPresetsIds();
        binding.emptyText.setVisibility(View.GONE);
        if (listPresetsUUIDs.length == 0) {
            binding.emptyText.setVisibility(View.VISIBLE);
            binding.presetList.deferNotifyDataSetChanged();
            binding.presetList.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return 0;
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
                    return null;
                }
            });
            return;
        }
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

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                UUID presetUUID = listPresetsUUIDs[position];
                Preset preset = presetList.getPreset(presetUUID);
                if (preset == null) {
                    Log.e("updateList", "get preset is null");
                    return new Button(PresetListActivity.this);
                }

                return getPresetView(presetUUID, preset);
            }
        });
    }

    /**
     * Получить View элемента списка
     * **/
    private View getPresetView(UUID presetUUID, Preset preset) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams checkboxLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        CheckBox checkBox = new CheckBox(this);
        checkBox.setChecked(preset.equals(informatorApp.getSelectedPreset()));
        checkBox.setTextSize(30);
        checkBox.setPadding(5, 5, 10, 5);
        checkBox.setLayoutParams(checkboxLayoutParams);
        checkBox.setOnClickListener(view -> {
            view.clearAnimation();
            checkBox.clearAnimation();
            informatorApp.setSelectedPreset(presetUUID);
            informatorApp.saveAppSchedule();
            updateList();
        });

        TextView textView = new TextView(this);
        PopupMenu popupMenu = new PopupMenu(this, textView);
        popupMenu.inflate(R.menu.menu_preset_list_popup);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.copy) {
                showCopyPresetDialog(presetUUID);

            } else if (item.getItemId() == R.id.delete) {
                showDeletePresetDialog(presetUUID);

            } else if (item.getItemId() == R.id.rename) {
                showRenamePresetDialog(presetUUID);
            }
            informatorApp.saveAppSchedule();
            return true;
        });
        textView.setText(preset == null ? "(null)" : preset.getName());
        textView.setTextSize(30);
        textView.setTextColor(Color.WHITE);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setOnClickListener(view -> startActivity(PresetEditActivity.getLaunchIntent(this, presetUUID)));
        textView.setOnLongClickListener(v -> {
            popupMenu.show();
            return true;
        });

        layout.addView(checkBox);
        layout.addView(textView);
        return layout;
    }
}