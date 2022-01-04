package ru.fazziclay.schoolguide.app;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.fazziclay.schoolguide.LaunchActivity;
import ru.fazziclay.schoolguide.app.multiplicationtrening.MultiplicationTreningApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.util.FileUtil;

public class SchoolGuideApp {
    public static final String JSON_EMPTY_OBJECT = "{}";

    public static SchoolGuideApp instance = null;

    public static SchoolGuideApp get() {
        if (instance == null) {
            instance = new SchoolGuideApp();
        }
        return instance;
    }

    private final SchoolGuideAndroidApp androidApp;
    private final ScheduleInformatorApp scheduleInformatorApp;
    private final MultiplicationTreningApp multiplicationTreningApp;

    DataFixer dataFixer;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Settings settings;

    public SchoolGuideApp() {
        androidApp = new SchoolGuideAndroidApp(this);
        scheduleInformatorApp = new ScheduleInformatorApp(this);
        multiplicationTreningApp = new MultiplicationTreningApp(this);
    }

    public void launch(LaunchActivity launchActivity) {
        dataFixer = new DataFixer(launchActivity);
        dataFixer.tryFix();
        Toast.makeText(launchActivity, "DETECTED_VERSION = "+dataFixer.tryGetVersion(), Toast.LENGTH_LONG).show();

        String externalPath = launchActivity.getExternalFilesDir(null).getAbsolutePath() + "/";

        settings = new Settings();
        try {
            settings = gson.fromJson(FileUtil.read(externalPath + Settings.FILE, JSON_EMPTY_OBJECT), Settings.class);
        } catch (Exception ignored) {}
        settings.filePath = externalPath + Settings.FILE;
        settings.save();
    }

    public void launchAndroidApp(Context context, LaunchActivity launchActivity) {
        androidApp.setContext(context);
        androidApp.launch(launchActivity);
    }

    public ScheduleInformatorApp getScheduleInformatorApp() {
        return this.scheduleInformatorApp;
    }

    public SchoolGuideAndroidApp getAndroidApp() {
        return this.androidApp;
    }

    public MultiplicationTreningApp getMultiplicationTreningApp() {
        return this.multiplicationTreningApp;
    }

    public Settings getSettings() {
        return settings;
    }

    public Gson getGson() {
        return gson;
    }
}
