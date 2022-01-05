package ru.fazziclay.schoolguide.app;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.datafixer.DataFixer;

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
    private final File filesFir;
    private final File cacheDir;

    private final Gson GSON;
    private final Settings settings;

    // Apps
    private final ScheduleInformatorApp scheduleInformatorApp;


    public SchoolGuideApp(Context context) {
        androidContext = context.getApplicationContext();
        filesFir = context.getExternalFilesDir(null);
        cacheDir = context.getExternalCacheDir();

        GSON = new GsonBuilder().setPrettyPrinting().create();

        DataFixer dataFixer = new DataFixer(this);
        dataFixer.tryFix();

        settings = Settings.load(new File(filesFir, Settings.FILE));
        settings.save();

        scheduleInformatorApp = new ScheduleInformatorApp(this);
    }

    public Context getAndroidContext() {
        return androidContext;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public File getFilesFir() {
        return filesFir;
    }

    public Gson getGson() {
        return GSON;
    }

    public Settings getSettings() {
        return settings;
    }

    public ScheduleInformatorApp getScheduleInformatorApp() {
        return this.scheduleInformatorApp;
    }
}
