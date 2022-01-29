package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import java.util.HashMap;
import java.util.UUID;

import ru.fazziclay.schoolguide.util.SortUtil;

public class PresetList {
    private final HashMap<UUID, Preset> presets = new HashMap<>();

    /**
     * Выдаёт пресет по его Айди, если нету то null
     * @return Пресет
     * **/
    public Preset getPreset(UUID uuid) {
        return presets.get(uuid);
    }

    /**
     * Вставляет по айди пресет в список, если под таким ключём уже есть, заменяет
     * **/
    public void putPreset(UUID uuid, Preset preset) {
        presets.put(uuid, preset);
    }

    /**
     * Выдаёт список айдишников пресетов, они отсортированы по имени пресета
     * **/
    public UUID[] getPresetsIds() {
        UUID[] ids = presets.keySet().toArray(new UUID[0]);
        SortUtil.sort(ids, uuid -> {
            Preset preset = getPreset(uuid);
            return preset.name;
        });
        return ids;
    }

    public Preset[] getPresets() {
        Preset[] presets = this.presets.values().toArray(new Preset[0]);
        SortUtil.sort(presets, preset -> preset.name);
        return presets;
    }

    public void removePreset(UUID uuid) {
        presets.remove(uuid);
    }
}
