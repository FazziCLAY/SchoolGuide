package ru.fazziclay.schoolguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.Settings;
import ru.fazziclay.schoolguide.app.global.AutoGlobalUpdateService;

public class SettingsActivity extends AppCompatActivity {
    private static final String KEY_IS_HIDE_EMPTY_NOTIFICATION = "isHideEmptyNotification";
    private static final String KEY_IS_FIRST_MONDAY = "isFirstMonday";
    private static final String KEY_IS_NOTIFICATION= "isNotification";
    private static final String KEY_IS_BUILTIN_PRESET_LIST = "isBuiltinPresetList";
    private static final String KEY_IS_DEVELOPER_FEATURES = "isDeveloperFeatures";
    private static final String KEY_NOTIFICATION_STATUS_TIME_BEFORE = "notificationStatusTimeBefore";
    private static final String KEY_PRESET_EDIT_NAME_IN_NEXT_LINE = "isPresetEditEventNameInNextLine";

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    private SchoolGuideApp app;
    private Settings settings;

    private SharedPreferences preferences;

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        boolean contains = sharedPreferences.contains(key);
        if (!contains || settings == null || app == null) return;

        if (KEY_IS_DEVELOPER_FEATURES.equals(key)) {
            settings.setDeveloperFeatures(sharedPreferences.getBoolean(key, false));
        } else if (KEY_IS_BUILTIN_PRESET_LIST.equals(key)) {
            settings.setBuiltinPresetList(sharedPreferences.getBoolean(key, false));
            AutoGlobalUpdateService.update(app);
        } else if (KEY_IS_HIDE_EMPTY_NOTIFICATION.equals(key)) {
            settings.setStopForegroundIsNone(sharedPreferences.getBoolean(key, false));
        } else if (KEY_IS_FIRST_MONDAY.equals(key)) {
            settings.setFirstMonday(sharedPreferences.getBoolean(key, false));
        } else if (KEY_IS_NOTIFICATION.equals(key)) {
            settings.setNotification(sharedPreferences.getBoolean(key, false));
        } else if (KEY_NOTIFICATION_STATUS_TIME_BEFORE.equals(key)) {
            try {
                settings.setNotificationStatusBeforeTime(Integer.parseInt(sharedPreferences.getString(key, "0")));
            } catch (Exception e) {
                app.getAppTrace().point("error while parse KEY_NOTIFICATION_STATUS_TIME_BEFORE settings", e);
            }
        } else if (KEY_PRESET_EDIT_NAME_IN_NEXT_LINE.equals(key)) {
            settings.setPresetEditEventNameInNextLine(sharedPreferences.getBoolean(key, false));
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
            Toast.makeText(this, "Error savedInstanceState is null!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .putBoolean(KEY_IS_HIDE_EMPTY_NOTIFICATION, settings.isStopForegroundIsNone())
                .putBoolean(KEY_IS_FIRST_MONDAY, settings.isFirstMonday())
                .putBoolean(KEY_IS_BUILTIN_PRESET_LIST, settings.isBuiltinPresetList())
                .putBoolean(KEY_IS_DEVELOPER_FEATURES, settings.isDeveloperFeatures())
                .putBoolean(KEY_IS_NOTIFICATION, settings.isNotification())
                .putString(KEY_NOTIFICATION_STATUS_TIME_BEFORE, String.valueOf(settings.getNotificationStatusBeforeTime()))
                .putBoolean(KEY_PRESET_EDIT_NAME_IN_NEXT_LINE, settings.isPresetEditEventNameInNextLine())
                .apply();

        preferences.registerOnSharedPreferenceChangeListener(listener);

        ImageButton shareAppTrace = findViewById(R.id.shareAppTrace);
        shareAppTrace.setOnClickListener(ignore -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "SchoolGuide debug appTrace");
                intent.putExtra(Intent.EXTRA_TEXT, app.getAppTrace().getText());
                startActivity(Intent.createChooser(intent, "Share to developer!"));
            } catch (Exception e) {
                app.getAppTrace().point("Error to share appTrace!", e);
                Toast.makeText(this, "Error to send appTrace!\n" + e, Toast.LENGTH_SHORT).show();
            }
        });
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