package ru.fazziclay.schoolguide.data.settings;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.DataBase;

public class Settings extends DataBase {
    public final static String SETTINGS_FILE = "settings.json";

    public static String getSettingsFilePath(Context context) {
        return context.getExternalFilesDir("").getAbsoluteFile() + "/" + SETTINGS_FILE;
    }

    public static Settings getSettings() {
        return ForegroundService.getInstance().getSettings();
    }

    @SerializedName("version")
    public int formatVersion = 1;
    public boolean notification = true;
    public boolean vibration = true;
    public boolean debug = false;
    public boolean useForegroundNotificationForMain = false;

    @Override
    public void save(String filePath) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        FileUtil.write(filePath, gson.toJson(this, Settings.class));
    }

}
