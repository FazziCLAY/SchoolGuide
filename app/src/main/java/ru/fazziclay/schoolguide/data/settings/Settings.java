package ru.fazziclay.schoolguide.data.settings;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.android.service.ForegroundService;

public class Settings {
    public final static String SETTINGS_FILE = "settings.json";

    public static String getSettingsFilePath(Context context) {
        return context.getExternalFilesDir("").getAbsoluteFile() + "/" + SETTINGS_FILE;
    }

    public static Settings getSettings() {
        return ForegroundService.getInstance().getSettings();
    }

    public static void save(Context context) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        FileUtil.write(getSettingsFilePath(context), gson.toJson(getSettings(), Settings.class));
    }

    @SerializedName("version")
    public int formatVersion = 1;
    public boolean notification = true;
    public boolean vibration = true;
    public boolean debug = false;
    public boolean useForegroundNotificationForMain = false;
}
