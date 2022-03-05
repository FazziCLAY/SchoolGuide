package ru.fazziclay.schoolguide.app.scheduleinformator;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.PresetList;

public class AppPresetList extends PresetList implements SelectablePresetList {
    private final Preset UNDEFINED_PRESET = new Preset("Undefined Preset");

    @SerializedName("selectedPreset")
    private UUID selectedPresetUUID = new UUID(0, 0);

    @Override
    public Preset getSelectedPreset() {
        Preset preset = getPreset(selectedPresetUUID);
        if (preset == null) {
            preset = getPreset(selectFirstByDisplayName());
        }

        if (preset == null) {
            preset = UNDEFINED_PRESET;
        }
        return preset;
    }

    @Override
    public void setSelectedPreset(UUID uuid) {
        selectedPresetUUID = uuid;
    }

    @Override
    public UUID getSelectedPresetId() {
        return selectedPresetUUID;
    }

    @Override
    public UUID selectFirstByDisplayName() {
        if (getPresetsIds().length > 0) {
            setSelectedPreset(getPresetsIds(true)[0]);
            return getSelectedPresetId();
        }
        return null;
    }
}
