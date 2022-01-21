package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import java.util.HashMap;
import java.util.UUID;

import ru.fazziclay.schoolguide.util.SortUtil;

public class Schedule {
    protected HashMap<UUID, Preset> presets = new HashMap<>();

    public Preset getPreset(UUID uuid) {
        return presets.get(uuid);
    }

    public void putPreset(UUID uuid, Preset preset) {
        presets.put(uuid, preset);
    }

    public UUID[] getPresetsUUIDs() {
        UUID[] uuids = presets.keySet().toArray(new UUID[0]);
        SortUtil.sort(uuids, o -> {
            Preset preset = getPreset((UUID) o);
            return preset.name;
        });
        return uuids;
    }

    public Preset[] getPresets() {
        Preset[] presets = this.presets.values().toArray(new Preset[0]);
        SortUtil.sort(presets, o -> ((Preset) o).name);
        return presets;
    }
}
