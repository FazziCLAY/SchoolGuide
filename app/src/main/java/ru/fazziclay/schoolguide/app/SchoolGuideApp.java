package ru.fazziclay.schoolguide.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.datafixer.DataFixer;
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

    private final Gson gson;
    private final Settings settings;

    private boolean isUpdateAvailable = false;

    // Apps
    private final ScheduleInformatorApp scheduleInformatorApp;


    public SchoolGuideApp(Context context) {
        androidContext = context.getApplicationContext();
        gson = new Gson();

        DataFixer dataFixer = new DataFixer(androidContext, SharedConstrains.APPLICATION_VERSION_CODE, SharedConstrains.DATA_FIXER_SCHEMES);
        dataFixer.fixIfAvailable();

        filesDir = context.getExternalFilesDir(null);
        cacheDir = context.getExternalCacheDir();

        settingsFile = new File(filesDir, "settings.json");
        settings = DataUtil.load(settingsFile, Settings.class);

        saveSettings();

        androidContext.startService(new Intent(androidContext, SchoolGuideService.class));
        androidContext.startService(new Intent(androidContext, UpdateCheckerService.class));

        scheduleInformatorApp = new ScheduleInformatorApp(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void registerNotificationChannels(Context context) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        List<NotificationChannel> channels = SharedConstrains.getNotificationChannels(context);

        for (NotificationChannel channel : channels) {
            notificationManager.createNotificationChannel(channel);
        }
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

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        isUpdateAvailable = updateAvailable;
    }
}
