package ru.fazziclay.schoolguide.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import ru.fazziclay.schoolguide.R;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_NOTIFICATION_IS_NOTIFICATION = "notification_isNotification";
    public static final String KEY_NOTIFICATION_IS_HIDE_EMPTY_NOTIFICATION = "notification_isHideEmptyNotification";
    public static final String KEY_NOTIFICATION_INFO_BEFORE_TIME = "notification_infoBeforeTime";
    public static final String KEY_PRESET_EDITOR_IS_FIRST_MONDAY = "presetEditor_isFirstMonday";
    public static final String KEY_PRESET_EDITOR_EVENT_NAME_IN_NEXT_LINE = "presetEditor_eventNameInNextLine";
    public static final String KEY_ADVANCED_IS_BUILTIN_PRESET_LIST = "advanced_isBuiltinPresetList";
    public static final String KEY_ADVANCED_IS_DEVELOPER_FEATURES = "advanced_isDeveloperFeatures";
    public static final String KEY_ADVANCED_SHARE_APP_TRACE = "advanced_shareAppTrace";

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    private SchoolGuideApp app;
    private Settings settings;
    private SharedPreferences preferences;

    private SharedPreferences.OnSharedPreferenceChangeListener changeListener = null;

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

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .putBoolean(KEY_NOTIFICATION_IS_NOTIFICATION, settings.isNotification())
                .putBoolean(KEY_NOTIFICATION_IS_HIDE_EMPTY_NOTIFICATION, settings.isHideEmptyNotification())
                .putString(KEY_NOTIFICATION_INFO_BEFORE_TIME, String.valueOf(settings.getNotificationStatusBeforeTime()))
                .putBoolean(KEY_PRESET_EDITOR_IS_FIRST_MONDAY, settings.isFirstMonday())
                .putBoolean(KEY_PRESET_EDITOR_EVENT_NAME_IN_NEXT_LINE, settings.isPresetEditEventNameInNextLine())
                .putBoolean(KEY_ADVANCED_IS_BUILTIN_PRESET_LIST, settings.isBuiltinPresetList())
                .putBoolean(KEY_ADVANCED_IS_DEVELOPER_FEATURES, settings.isDeveloperFeatures())
                .apply();

        changeListener = (s, key) -> {
            if (s == null || key == null) {
                app.getAppTrace().point("onChangeListener: sharedPreference || key == null! key="+key);
                return;
            }
            boolean isKeyContains = s.contains(key);
            if (!isKeyContains || app == null || settings == null) return;


            switch (key) {
                case KEY_NOTIFICATION_IS_NOTIFICATION:
                    settings.setNotification(s.getBoolean(key, false));
                    break;

                case KEY_NOTIFICATION_IS_HIDE_EMPTY_NOTIFICATION:
                    settings.setIsHideEmptyNotification(s.getBoolean(key, false));
                    break;

                case KEY_NOTIFICATION_INFO_BEFORE_TIME:
                    int i = 10800;
                    try {
                        i = Integer.parseInt(s.getString(key, "-1"));
                    } catch (Exception e) {
                        app.getAppTrace().point("Error while parse integer in settings KEY_NOTIFICATION_STATUS_TIME_BEFORE", e);
                    }
                    settings.setNotificationStatusBeforeTime(i);
                    break;

                case KEY_PRESET_EDITOR_IS_FIRST_MONDAY:
                    settings.setFirstMonday(s.getBoolean(key, false));
                    break;

                case KEY_PRESET_EDITOR_EVENT_NAME_IN_NEXT_LINE:
                    settings.setPresetEditEventNameInNextLine(s.getBoolean(key, false));
                    break;

                case KEY_ADVANCED_IS_BUILTIN_PRESET_LIST:
                    settings.setBuiltinPresetList(s.getBoolean(key, false));
                    break;

                case KEY_ADVANCED_IS_DEVELOPER_FEATURES:
                    settings.setDeveloperFeatures(s.getBoolean(key, false));
                    break;

                default:
                    app.getAppTrace().point("Unknown settings key!\nkey="+key);
                    break;
            }
            app.getOnUserChangeSettingsCallbacks().run((callbackStorage, callback) -> callback.run(key));
            app.saveSettings();
        };
        preferences.registerOnSharedPreferenceChangeListener(changeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(changeListener);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);
            Preference shareAppTracePreference = getPreferenceManager().findPreference(KEY_ADVANCED_SHARE_APP_TRACE);
            if (shareAppTracePreference != null) {
                shareAppTracePreference.setOnPreferenceClickListener(preference -> {
                    showShareAppTraceDialog();
                    return true;
                });
            }
        }

        private void showShareAppTraceDialog() {
            SchoolGuideApp app = SchoolGuideApp.get(getActivity());
            if (app == null) return;
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "SchoolGuide debug info");
                intent.putExtra(Intent.EXTRA_TEXT, app.getAppTrace().getText());
                startActivity(Intent.createChooser(intent, "Share to developer!"));
            } catch (Exception e) {
                app.getAppTrace().point("Error while share appTrace!", e);
                Toast.makeText(getActivity(), "Error while send appTrace!\n" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }
}