package ru.fazziclay.schoolguide.app.scheduleinformator;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Schedule;

public class AppSchedule extends Schedule {
    @SerializedName("selectedPreset")
    private UUID selectedPresetUUID = new UUID(0, 0);

    public Preset getSelectedPreset() {
        if (selectedPresetUUID == null) {
            selectedPresetUUID = new UUID(0, 0);
        }
        Preset preset = getPreset(selectedPresetUUID);
        if (preset == null) {
            preset = getPreset(selectFirst());
            if (preset != null) return preset;
            preset = new Preset();
            preset.setName("(PRESET_NULL)");
            Log.e("ERROR", "getCurrentPreset: null");
        }
        return preset;
    }

    public void setSelected(UUID uuid) {
        selectedPresetUUID = uuid;
    }

    public UUID selectFirst() {
        if (getPresetsUUIDs().length > 0) {
            selectedPresetUUID = getPresetsUUIDs()[0];
            return selectedPresetUUID;
        }
        return null;
    }
}
