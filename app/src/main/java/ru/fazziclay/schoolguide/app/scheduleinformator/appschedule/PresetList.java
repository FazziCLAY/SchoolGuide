package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import java.util.HashMap;
import java.util.UUID;

import ru.fazziclay.schoolguide.util.SortUtil;
import ru.fazziclay.schoolguide.util.UUIDUtil;

public class PresetList {
    private HashMap<UUID, Preset> presets = new HashMap<>();

    private void fix() {
        if (presets == null) presets = new HashMap<>();
    }

    /**
     * Выдаёт пресет по его Айди, если нету то null
     * @return Пресет
     * **/
    public Preset getPreset(UUID uuid) {
        fix();
        return presets.get(uuid);
    }

    /**
     * Вставляет по айди пресет в список, если под таким ключём уже есть, заменяет
     * **/
    public void putPreset(UUID uuid, Preset preset) {
        fix();
        presets.put(uuid, preset);
    }

    /**
     * Добавить пресет в список, возвращает его ID
     * **/
    public UUID addPreset(Preset preset) {
        fix();
        for (Preset p : presets.values()) {
            if (p == preset) throw new RuntimeException("preset exist in preset list!");
        }
        UUID id = UUIDUtil.generateUUID(presets.keySet().toArray(new UUID[0]));
        putPreset(id, preset);
        return id;
    }

    /**
     * Выдаёт список айдишников пресетов, они отсортированы по имени пресета
     * **/
    public UUID[] getPresetsIds() {
        fix();
        UUID[] ids = presets.keySet().toArray(new UUID[0]);
        SortUtil.sort(ids, uuid -> {
            Preset preset = getPreset(uuid);
            return preset.getName();
        });
        return ids;
    }

    public Preset[] getPresets() {
        fix();
        Preset[] presets = this.presets.values().toArray(new Preset[0]);
        SortUtil.sort(presets, Preset::getName);
        return presets;
    }

    public void removePreset(UUID uuid) {
        fix();
        presets.remove(uuid);
    }
}
