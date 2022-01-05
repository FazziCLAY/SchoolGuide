package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import ru.fazziclay.schoolguide.util.data.BaseData;

public class AppSchedule extends BaseData {
    public static final String FILE = "schoolguide.app_schedule.json";

    public static AppSchedule load(File file) {
        return (AppSchedule) load(file, AppSchedule.class);
    }

    public AppSchedule(Gson gson, String filePath) {
        super(gson, filePath);
    }

    UUID selectedPreset = new UUID(0, 0);
    HashMap<UUID, Preset> presets = new HashMap<>();

    public Preset getPreset(UUID uuid) {
        return presets.get(uuid);
    }

    public void putPreset(UUID uuid, Preset preset) {
        presets.put(uuid, preset);
    }

    public void setSelectedPreset(UUID selectedPreset) {
        this.selectedPreset = selectedPreset;
    }

    public UUID getSelectedPreset() {
        return selectedPreset;
    }

    public HashMap<UUID, Preset> getPresets() {
        return this.presets;
    }
}
