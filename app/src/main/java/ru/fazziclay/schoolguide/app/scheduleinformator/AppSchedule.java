package ru.fazziclay.schoolguide.app.scheduleinformator;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Schedule;

public class AppSchedule extends Schedule {

    @SerializedName("currentPreset")
    UUID currentPresetUUID = new UUID(0, 0);

    public Preset getCurrentPreset() {
        if (currentPresetUUID == null) {
            currentPresetUUID = new UUID(0, 0);
        }
        Preset preset = getPreset(currentPresetUUID);
        if (preset == null) {
            preset = getPreset(selectFirst());
            if (preset != null) return preset;
            preset = new Preset();
            preset.setName("(PRESET_NULL)");
            Log.e("ERROR", "getCurrentPreset: null");
        }
        return preset;
    }

    public void setCurrent(UUID uuid) {
        currentPresetUUID = uuid;
    }

    public UUID selectFirst() {
        if (getPresetsUUIDs().length > 0) {
            currentPresetUUID = getPresetsUUIDs()[0];
            return currentPresetUUID;
        }
        return null;
    }
}
