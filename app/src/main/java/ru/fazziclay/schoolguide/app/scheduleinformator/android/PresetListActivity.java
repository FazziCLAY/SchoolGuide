package ru.fazziclay.schoolguide.app.scheduleinformator.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.SpannableString;
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

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.app.DebugActivity;
import ru.fazziclay.schoolguide.app.MilkLog;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.SettingsActivity;
import ru.fazziclay.schoolguide.app.SharedConstrains;
import ru.fazziclay.schoolguide.app.UpdateCenterActivity;
import ru.fazziclay.schoolguide.app.listener.OnDebugSignalListener;
import ru.fazziclay.schoolguide.app.listener.OnGlobalUpdatedListener;
import ru.fazziclay.schoolguide.app.listener.OnUserSettingsChangeListener;
import ru.fazziclay.schoolguide.app.listener.PresetListUpdateSignalListener;
import ru.fazziclay.schoolguide.app.multiplicationtrening.MathTreningGameActivity;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.SelectablePresetList;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.PresetList;
import ru.fazziclay.schoolguide.callback.CallbackImportance;
import ru.fazziclay.schoolguide.callback.Status;
import ru.fazziclay.schoolguide.databinding.ActivityPresetListBinding;
import ru.fazziclay.schoolguide.util.ColorUtil;

public class PresetListActivity extends AppCompatActivity {
    public static final int PRESET_NAME_MAX_LENGTH = 25;
    public static final int PRESET_NAME_MAX_LINES = 1;

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, PresetListActivity.class);
    }

    private SchoolGuideApp app;
    private ScheduleInformatorApp informatorApp;
    private ActivityPresetListBinding binding;

    // Menu
    private MenuItem openUpdateCenterMenuItem;
    private MenuItem openDebugItem;

    // target (always AppPresetList from informatorApp)
    private PresetList presetList;

    // Callbacks
    private PresetListUpdateSignalListener presetListUpdateSignalListener;
    private OnDebugSignalListener onDebugSignalListener;
    private OnUserSettingsChangeListener onUserSettingsChangeListener;
    private OnGlobalUpdatedListener onGlobalUpdatedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        if (app == null) {
            setContentView(SharedConstrains.getAppNullView(this));
            return;
        }
        informatorApp = app.getScheduleInformatorApp();
        presetList = informatorApp.getAppPresetList();

        binding = ActivityPresetListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.addPreset.setOnClickListener(ignore -> showCreateNewPresetDialog());

        registerListeners();
        setupListAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showDisableBatteryOptimizationDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unbind callbacks
        app.getPresetListUpdateCallbacks().deleteCallback(presetListUpdateSignalListener);
        app.getDebugSignalListenerCallbacks().deleteCallback(onDebugSignalListener);
        app.getOnUserChangeSettingsCallbacks().deleteCallback(onUserSettingsChangeListener);
        app.getGlobalUpdateCallbacks().deleteCallback(onGlobalUpdatedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        openUpdateCenterMenuItem = menu.findItem(R.id.openUpdateCenterItem);
        openDebugItem = menu.findItem(R.id.openDebugItem);
        updateOpenUpdateCenterMenuName();
        if (openDebugItem != null) {
            openDebugItem.setVisible(app.getSettings().isDeveloperFeatures());
        }
        return true;
    }

    private void registerListeners() {
        presetListUpdateSignalListener = () -> {
            try {
                if (!isFinishing()) {
                    runOnUiThread(() -> {
                        try {
                            setupListAdapter();
                        } catch (Exception ignored) {}
                    });
                }
            } catch (Exception ignored) {}

            return new Status.Builder()
                    .setDeleteCallback(isFinishing())
                    .build();
        };
        app.getPresetListUpdateCallbacks().addCallback(CallbackImportance.DEFAULT, presetListUpdateSignalListener);


        onDebugSignalListener = data -> {
            runOnUiThread(() -> {
                TextView textView = new TextView(this);
                textView.setText(String.format("DEBUG_SIGNAL: %s", data));
                binding.notificationContainer.addView(textView);
            });
            runOnUiThread(() -> Toast.makeText(this, "Debug signal! " + data, Toast.LENGTH_SHORT).show());
            return new Status.Builder()
                    .build();
        };
        app.getDebugSignalListenerCallbacks().addCallback(CallbackImportance.DEFAULT, onDebugSignalListener);


        onUserSettingsChangeListener = (preferenceKey) -> {
            if (preferenceKey.equals(SettingsActivity.KEY_ADVANCED_IS_DEVELOPER_FEATURES)) {
                runOnUiThread(() -> {
                    if (openDebugItem != null) {
                        openDebugItem.setVisible(app.getSettings().isDeveloperFeatures());
                    }
                });
            }
            return new Status.Builder()
                    .build();
        };
        app.getOnUserChangeSettingsCallbacks().addCallback(CallbackImportance.DEFAULT, onUserSettingsChangeListener);

        onGlobalUpdatedListener = (globalKeys, globalVersionManifest, globalBuiltinPresetList) -> {
            runOnUiThread(this::updateOpenUpdateCenterMenuName);
            return new Status.Builder()
                    .build();
        };
        app.getGlobalUpdateCallbacks().addCallback(CallbackImportance.DEFAULT, onGlobalUpdatedListener);
    }

    private void updateOpenUpdateCenterMenuName() {
        if (openUpdateCenterMenuItem == null) return;
        if (app.isUpdateAvailable()) {
            openUpdateCenterMenuItem.setTitle(ColorUtil.colorize(getString(R.string.mainOptionMenu_openUpdateCenter_available), Color.RED, Color.TRANSPARENT, Typeface.BOLD));
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
                        MilkLog.g("failed start DISABLE BATTERY OPTIMIZATION dialog", e);
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
                        Toast.makeText(this, R.string.presetList_exception_presetNameEmpty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    presetList.addPreset(new Preset(newName));
                    informatorApp.saveAppSchedule();
                    setupListAdapter();
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
            MilkLog.g("showDeletePresetDialog: preset null by uuid; uuid=" + uuid.toString());
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.presetList_delete_title, preset.getName()))
                .setMessage(getString(R.string.presetList_delete_message))
                .setPositiveButton(R.string.presetList_delete, (dialogInterface, which) -> {
                    if (presetList instanceof SelectablePresetList) {
                        SelectablePresetList selectablePresetList = (SelectablePresetList) presetList;
                        boolean selected = selectablePresetList.getSelectedPreset() == preset;
                        presetList.removePreset(uuid);
                        if (selected) selectablePresetList.selectFirst();
                    } else {
                        presetList.removePreset(uuid);
                    }
                    informatorApp.saveAppSchedule();
                    setupListAdapter();
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
            MilkLog.g("showCopyPresetDialog: preset null by uuid; uuid=" + uuid.toString());
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText name = new EditText(this);
        name.setPadding(13, 10, 13, 10);
        name.setHint(R.string.presetList_copy_nameHint);
        name.setText(getString(R.string.presetList_copy_copyPattern, preset.getName()));
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
                        Toast.makeText(this, R.string.presetList_exception_presetNameEmpty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Preset newPreset = preset.clone();
                    newPreset.setSyncedByGlobal(false);
                    newPreset.setName(newName);
                    presetList.addPreset(newPreset);
                    informatorApp.saveAppSchedule();
                    setupListAdapter();
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
            MilkLog.g("showRenamePresetDialog: preset null by uuid; uuid=" + uuid.toString());
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

        String message = getString(R.string.presetList_rename_message);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.presetList_rename_title, preset.getName()))
                .setMessage(message.isEmpty() ? null : message)
                .setView(layout)
                .setPositiveButton(R.string.presetList_rename, (e, e1) -> {
                    String newName = name.getText().toString();
                    if (newName.isEmpty()) {
                        Toast.makeText(this, R.string.presetList_exception_presetNameEmpty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    preset.setName(newName);
                    informatorApp.saveAppSchedule();
                    binding.presetList.deferNotifyDataSetChanged();
                })
                .setNegativeButton(R.string.presetList_rename_cancel, null)
                .create();

        dialog.show();
    }

    /**
     * Обновить View список
     * **/
    private void setupListAdapter() {
        binding.presetList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                if (presetList.getPresetsIds().length > 0) {
                    binding.emptyText.setVisibility(View.GONE);
                } else {
                    binding.emptyText.setVisibility(View.VISIBLE);
                }
                return presetList.getPresetsIds().length;
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
                if (position >= presetList.getPresetsIds().length) {
                    MilkLog.g("adapter list.len >= position!");
                    return new Button(PresetListActivity.this);
                }
                UUID presetUUID = presetList.getPresetsIds()[position];
                Preset preset = presetList.getPreset(presetUUID);
                if (preset == null) {
                    MilkLog.g("adapter get view preset == null!");
                    return new Button(PresetListActivity.this);
                }

                return getPresetView(presetUUID, preset);
            }
        });
    }

    /**
     * Получить View элемента списка
     * **/
    private CheckBox previousCheckedCheckbox = null;
    private View getPresetView(UUID presetUUID, Preset preset) {
        if (presetUUID == null || preset == null) {
            MilkLog.g("Warning! getPresetView received null args! returned TextView witch text presetList_error_getPresetView_nullArgs\n@presetUUID="+presetUUID + "\n@preset="+preset, new NullPointerException("By fazziclay!"));
            TextView textView = new TextView(this);
            textView.setTextColor(Color.RED);
            textView.setText(R.string.presetList_error_getPresetView_nullArgs);
            return textView;
        }
        boolean isGlobal = preset.isSyncedByGlobal();
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams checkboxLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        CheckBox checkBox = new CheckBox(this);
        checkBox.setChecked(preset.equals(informatorApp.getSelectedPreset()));
        if (checkBox.isChecked()) previousCheckedCheckbox = checkBox;
        checkBox.setTextSize(30);
        checkBox.setPadding(5, 5, 10, 5);
        checkBox.setLayoutParams(checkboxLayoutParams);
        checkBox.setOnClickListener(view -> {
            if (previousCheckedCheckbox == checkBox) {
                checkBox.setChecked(true);
                return;
            }
            previousCheckedCheckbox.setChecked(false);
            previousCheckedCheckbox = checkBox;
            view.clearAnimation();
            informatorApp.setSelectedPreset(presetUUID);
            informatorApp.saveAppSchedule();
            binding.presetList.deferNotifyDataSetChanged();
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
            return true;
        });
        popupMenu.getMenu().findItem(R.id.delete).setEnabled(!isGlobal);
        popupMenu.getMenu().findItem(R.id.rename).setEnabled(!isGlobal);

        SpannableString string;
        if (isGlobal) {
            string = ColorUtil.colorize(preset.getName(), Color.parseColor("#ffaaaaaa"), Color.TRANSPARENT, Typeface.ITALIC);
        } else {
            string = ColorUtil.colorize(preset.getName(), Color.parseColor("#ffffff"), Color.TRANSPARENT, Typeface.NORMAL);
        }
        textView.setText(string);
        textView.setTextSize(30);
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