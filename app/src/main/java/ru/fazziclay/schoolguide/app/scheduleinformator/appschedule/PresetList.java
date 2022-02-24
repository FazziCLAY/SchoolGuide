package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.UUID;

import ru.fazziclay.schoolguide.util.SortUtil;
import ru.fazziclay.schoolguide.util.UUIDUtil;

/**
 * <h1>Список пресетов</h1>
 * **/
public class PresetList {
    /**
     * Name "presets" since pre v50
     * **/
    @SerializedName("presets")
    private HashMap<UUID, Preset> presets = new HashMap<>();

    /**
     * Исправить переменные для корректной работы
     * (пустая HashMap если она вообще null)
     * **/
    private void fix() {
        if (presets == null) presets = new HashMap<>();
    }

    /**
     * Выдаёт пресет по его Айди, если нету то null
     * @return Значение по ключу
     * **/
    public Preset getPreset(UUID uuid) {
        fix();
        return presets.get(uuid);
    }

    /**
     * Вставляет пресет в HashMap с его Айди, если под таким ключём уже есть, заменяет
     * **/
    public void putPreset(UUID uuid, Preset preset) {
        fix();
        presets.put(uuid, preset);
    }

    /**
     * Добавить пресет в список, возвращает его Айди (генерируется сам)
     * @throws RuntimeException если такой же (x == x1) уже есть
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
     * Выдаёт список доступных пресетов в их айдишниках
     * @param sort отсортировать ли список по отображаемому имени пресета
     * **/
    public UUID[] getPresetsIds(boolean sort) {
        fix();
        UUID[] ids = presets.keySet().toArray(new UUID[0]);
        if (sort) {
            SortUtil.sort(ids, uuid -> {
                Preset preset = getPreset(uuid);
                if (preset == null) {
                    return null; // IDE Fix: на практике такого никогда не должно произайти
                }
                return preset.getName();
            });
        }
        return ids;
    }

    /**
     * no-sort
     * @see PresetList#getPresetsIds(boolean)
     * **/
    public UUID[] getPresetsIds() {
        return getPresetsIds(false);
    }

    /**
     * Выдаёт список доступных пресетов
     * @param sort отсортировать ли список по отображаемому имени пресета
     * **/
    public Preset[] getPresets(boolean sort) {
        fix();
        Preset[] presets = this.presets.values().toArray(new Preset[0]);
        if (sort) SortUtil.sort(presets, Preset::getName);
        return presets;
    }

    public void removePreset(UUID uuid) {
        fix();
        presets.remove(uuid);
    }
}
