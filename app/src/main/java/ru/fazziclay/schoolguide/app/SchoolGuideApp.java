package ru.fazziclay.schoolguide.app;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.UUID;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.app.manifest.GlobalVersionManifest;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Schedule;
import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.datafixer.schem.v33to35.SchemePre36To36;
import ru.fazziclay.schoolguide.util.DataUtil;

public class SchoolGuideApp {
    public static SchoolGuideApp instance = null;

    public static boolean isInstanceAvailable() {
        return instance != null;
    }

    public static SchoolGuideApp get(Context context) {
        if (!isInstanceAvailable()) {
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

    private Gson gson;
    private final Settings settings;

    private final long latestAutoManifestUpdate = 0;
    private GlobalVersionManifest globalVersionManifest;

    // Apps
    private final ScheduleInformatorApp scheduleInformatorApp;


    public SchoolGuideApp(Context context) {
        androidContext = context.getApplicationContext();
        gson = new Gson();

        DataFixer dataFixer = new DataFixer(androidContext, SharedConstrains.APPLICATION_VERSION_CODE, new AbstractScheme[]{
            new SchemePre36To36()
        });
        dataFixer.fixIfAvailable();

        filesDir = context.getExternalFilesDir(null);
        cacheDir = context.getExternalCacheDir();

        settingsFile = new File(filesDir, "settings.json");
        settings = DataUtil.load(settingsFile, Settings.class);

        if (!settings.storageSpaceSaving) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        }

        saveSettings();

        if (System.currentTimeMillis() - latestAutoManifestUpdate > 60*60*1000) {

        }

        scheduleInformatorApp = new ScheduleInformatorApp(this);
    }

    interface SelectPresetDialogInterface {
        void selected(Schedule schedule, UUID[] selected);
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
        return gson;
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
