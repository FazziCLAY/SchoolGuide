package ru.fazziclay.schoolguide.data.settings;

import com.google.gson.Gson;

import android.content.Context;

import java.util.UUID;

import ru.fazziclay.schoolguide.util.FileUtil;
import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.BaseProvider;

public class SettingsProvider extends BaseProvider {
    private static final String SETTINGS_FILE = "settings.json";

    public SettingsProvider(Context context) {
        filePath = context.getExternalFilesDir(null).getAbsolutePath().concat("/").concat(SETTINGS_FILE);
        data = load();

        if (data.isFormatVersionDefault()) data.formatVersion = 2;
        save();
    }

    public Settings getSettings() {
        return (Settings) data;
    }

    public void setNotification(boolean s) {
        getSettings().isNotification = s;
        save();
    }

    public boolean isNotification() {
        return getSettings().isNotification;
    }

    public void setVibration(boolean s) {
        getSettings().isVibration = s;
        save();
    }
    public boolean isVibration() {
        return getSettings().isVibration;
    }

    public void getUserNotification(UserNotification s) {
        getSettings().userNotification = s;
        save();
    }

    public UserNotification getUserNotification() {
        return getSettings().userNotification;
    }

    public void setUserNotification(UserNotification s) {
        getSettings().userNotification = s;
        save();
    }

    public void setSelectedLocalSchedule(UUID uuid) {
        getSettings().selectedLocalSchedule = uuid;
        save();
    }

    public UUID getSelectedLocalSchedule() {
        return getSettings().selectedLocalSchedule;
    }

    public DeveloperSettings getDeveloperSettings() {
        return getSettings().developerSettings;
    }

    public AppTheme getTheme() {
        return getSettings().theme;
    }

    public void setTheme(AppTheme theme) {
        getSettings().theme = theme;
        save();
    }

    public void setFirstWeekDay(int firstWeekDay) {
        getSettings().firstWeekDay = firstWeekDay;
        save();
    }

    public int getFirstWeekDay() {
        return getSettings().firstWeekDay;
    }

    @Override
    public BaseData load() {
        Gson gson = new Gson();
        return gson.fromJson(FileUtil.read(filePath, "{}"), Settings.class);
    }
}
