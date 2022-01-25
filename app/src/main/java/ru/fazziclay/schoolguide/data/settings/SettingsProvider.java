package ru.fazziclay.schoolguide.data.settings;

import com.google.gson.Gson;

import android.content.Context;

import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.SchoolGuide;
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

    @Override
    public BaseData load() {
        Gson gson = new Gson();
        return gson.fromJson(FileUtil.read(filePath, "{}"), Settings.class);
    }

    public Settings getSettings() {
        return (Settings) data;
    }

    public void setVibration(boolean s) {
        getSettings().isVibration = s;
        save();
    }
    public boolean isVibration() {
        return getSettings().isVibration;
    }

    public void setSelectedLocalSchedule(UUID uuid) {
        getSettings().selectedLocalSchedule = uuid;
        save();
        SchoolGuide.getInstance().updateSelectedLocalSchedule();
    }

    public UUID getSelectedLocalSchedule() {
        return getSettings().selectedLocalSchedule;
    }

    public List<Integer> getVersionsHistory() {
        return getSettings().versionsHistory;
    }

    public void addVersionsHistory(int version) {
        if (!getSettings().versionsHistory.contains(version)) {
            getSettings().versionsHistory.add(version);
            save();
        }
    }

    public boolean isDeveloperFeatures() {
        return getSettings().isDeveloperFeatures;
    }


    public boolean isSyncDeveloperSchedule() {
        return getSettings().isSyncDeveloperSchedule;
    }

    public void setSyncDeveloperSchedule(boolean syncDeveloperSchedule) {
        getSettings().isSyncDeveloperSchedule = syncDeveloperSchedule;
        save();
    }

    public NotificationStyle getNotificationStyle() {
        return getSettings().notificationStyle;
    }


    public int getNotifyBeforeTime() {
        return getSettings().notifyBeforeTime;
    }

    public void setNotifyBeforeTime(int notifyBeforeTime) {
        getSettings().notifyBeforeTime = notifyBeforeTime;
        save();
    }

}