package ru.fazziclay.schoolguide.app.scheduleinformator;

import java.util.UUID;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;

public interface SelectablePresetList {
    Preset getSelectedPreset();

    void setSelectedPreset(UUID uuid);

    UUID getSelectedPresetId();

    UUID selectFirst();
}
