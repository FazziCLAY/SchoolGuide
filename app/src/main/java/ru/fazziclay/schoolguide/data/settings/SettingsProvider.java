package ru.fazziclay.schoolguide.data.settings;

import com.google.gson.Gson;

import android.content.Context;
import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.BaseProvider;

public class SettingsProvider extends BaseProvider {
    private static final String SETTINGS_FILE = "settings.json";

    public SettingsProvider(Context context) {
        filePath = context.getExternalFilesDir(null).getAbsolutePath().concat("/").concat(SETTINGS_FILE);
        data = load();
    }

    public void setNotification(boolean s) {
        ((Settings) data).notification = s;
        save();
    }

    public boolean isNotification() {
        return ((Settings) data).notification;
    }

    public void setVibration(boolean s) {
        ((Settings) data).vibration = s;
        save();
    }
    public boolean isVibration() {
        return ((Settings) data).vibration;
    }

    public void setUseForegroundNotificationForMain(boolean s) {
        ((Settings) data).useForegroundNotificationForMain = s;
        save();
    }

    public boolean isUseForegroundNotificationForMain() {
        return ((Settings) data).useForegroundNotificationForMain;
    }

    @Override
    public BaseData load() {
        Gson gson = new Gson();
        return gson.fromJson(FileUtil.read(filePath, "{}"), Settings.class);
    }
}
