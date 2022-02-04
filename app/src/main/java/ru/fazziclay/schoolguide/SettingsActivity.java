package ru.fazziclay.schoolguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.Settings;
import ru.fazziclay.schoolguide.app.global.AutoGlobalUpdateService;
import ru.fazziclay.schoolguide.app.listener.PresetListUpdateListener;
import ru.fazziclay.schoolguide.callback.CallbackStorage;
import ru.fazziclay.schoolguide.callback.Status;

public class SettingsActivity extends AppCompatActivity {
    private static final String KEY_IS_DEVELOPER_FEATURES = "isDeveloperFeatures";
    private static final String KEY_IS_BUILTIN_PRESET_LIST = "isBuiltinPresetList";
    private static final String KEY_IS_SHOW_EMPTY_NOTIFICATION = "isShowEmptyNotification";
    private static final String KEY_IS_FIRST_MONDAY = "isFirstMonday";

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    private SchoolGuideApp app;
    private Settings settings;

    private SharedPreferences preferences;

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        boolean contains = sharedPreferences.contains(key);
        if (!contains) return;

        if (KEY_IS_DEVELOPER_FEATURES.equals(key)) {
            settings.isDeveloperFeatures = sharedPreferences.getBoolean(key, false);
        } else if (KEY_IS_BUILTIN_PRESET_LIST.equals(key)) {
            settings.isBuiltInPresetList = sharedPreferences.getBoolean(key, false);
            AutoGlobalUpdateService.update(app);
        } else if (KEY_IS_SHOW_EMPTY_NOTIFICATION.equals(key)) {
            settings.isStopForegroundIsNone = !sharedPreferences.getBoolean(key, false);
        } else if (KEY_IS_FIRST_MONDAY.equals(key)) {
            settings.isFirstMonday = sharedPreferences.getBoolean(key, false);
        }
        app.saveSettings();
        app.getPresetListUpdateCallbacks().run((callbackStorage, callback) -> callback.onPresetListUpdate());
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = SchoolGuideApp.get(this);
        if (app == null) {
            setContentView(SharedConstrains.getAppNullView(this));
            return;
        }
        settings = app.getSettings();

        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings_activityTitle);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .putBoolean(KEY_IS_SHOW_EMPTY_NOTIFICATION, !settings.isStopForegroundIsNone)
                .putBoolean(KEY_IS_DEVELOPER_FEATURES, settings.isDeveloperFeatures)
                .putBoolean(KEY_IS_BUILTIN_PRESET_LIST, settings.isBuiltInPresetList)
                .putBoolean(KEY_IS_FIRST_MONDAY, settings.isFirstMonday)
                .apply();

        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);
        }
    }
}