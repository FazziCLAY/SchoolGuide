package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.UUID;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.util.FileUtil;

public class AppSchedule {
    public static final String FILE = "schoolguide.app_schedule.json";

    public static AppSchedule load(String filePath) {
        Gson gson = new Gson();
        AppSchedule appSchedule = gson.fromJson(FileUtil.read(filePath, SchoolGuideApp.JSON_EMPTY_OBJECT), AppSchedule.class);
        appSchedule.filePath = filePath;

        return appSchedule;
    }

    public transient String filePath;
    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileUtil.write(filePath, gson.toJson(this, this.getClass()));
    }

    public HashMap<UUID, Preset> presets = new HashMap<>();

    public Preset getPreset(UUID uuid) {
        return presets.get(uuid);
    }

    public void putPreset(UUID uuid, Preset preset) {
        presets.put(uuid, preset);
    }
}
