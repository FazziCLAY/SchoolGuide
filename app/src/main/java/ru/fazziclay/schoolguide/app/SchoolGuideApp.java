package ru.fazziclay.schoolguide.app;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.util.DataUtil;

public class SchoolGuideApp {
    public static SchoolGuideApp instance = null;

    public static SchoolGuideApp get(Context context) {
        if (instance == null) {
            instance = new SchoolGuideApp(context);
        }
        return instance;
    }

    public static SchoolGuideApp get() {
        return get(null);
    }

    // Android
    private final Context androidContext;
    private final File filesDir;
    private final File cacheDir;

    private final File settingsFile;

    private final Gson GSON;
    private final Settings settings;

    // Apps
    private final ScheduleInformatorApp scheduleInformatorApp;


    public SchoolGuideApp(Context context) {
        androidContext = context.getApplicationContext();
        GSON = new GsonBuilder().setPrettyPrinting().create();

        DataFixer dataFixer = new DataFixer(androidContext, GSON);
        dataFixer.tryFix();

        filesDir = context.getExternalFilesDir(null);
        cacheDir = context.getExternalCacheDir();

        settingsFile = new File(filesDir, "schoolguide.settings.json");
        settings = (Settings) DataUtil.load(settingsFile, Settings.class);
        saveSettings();

        scheduleInformatorApp = new ScheduleInformatorApp(this);
    }

    public void saveSettings() {
        DataUtil.save(settingsFile, settings);
    }

    public Context getAndroidContext() {
        return androidContext;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public File getFilesDir() {
        return filesDir;
    }

    public Gson getGson() {
        return GSON;
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public Settings getSettings() {
        return settings;
    }

    public ScheduleInformatorApp getScheduleInformatorApp() {
        return this.scheduleInformatorApp;
    }
}
