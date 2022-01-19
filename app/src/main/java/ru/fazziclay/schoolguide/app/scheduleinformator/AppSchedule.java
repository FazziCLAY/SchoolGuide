package ru.fazziclay.schoolguide.app.scheduleinformator;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Schedule;

public class AppSchedule extends Schedule {
    transient Preset selectedPreset = null;

    @SerializedName("selectedPreset")
    UUID selectedPresetUUID = new UUID(0, 0);

    public void setSelectedPreset(Preset selectedPreset) {
        if (presets.containsValue(selectedPreset)) {
            this.selectedPreset = selectedPreset;
            this.selectedPresetUUID = getUUIDByPreset(selectedPreset);
        }
    }

    public Preset getSelectedPreset() {
        if (selectedPreset == null) {
            selectedPreset = new Preset();
        }
        return selectedPreset;
    }

    public UUID getUUIDByPreset(Preset preset) {
        for (UUID key : presets.keySet()) {
            if (presets.get(key) == preset) {
                return key;
            }
        }
        return null;
    }
}
