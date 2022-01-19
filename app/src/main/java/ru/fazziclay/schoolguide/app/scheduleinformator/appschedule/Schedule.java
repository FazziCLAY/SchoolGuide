package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import java.util.HashMap;
import java.util.UUID;

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
        sort(uuids, o -> {
            UUID uuid = (UUID) o;
            Preset p = presets.get(uuid);
            return p.name;
        });
        return uuids;
    }

    public Preset[] getPresets() {
        Preset[] presets = this.presets.values().toArray(new Preset[0]);
        sort(presets, o -> ((Preset) o).name);
        return presets;
    }

    public void sort(Object[] list, Stringer stringer) {
        int n = list.length;
        Object temp;

        for (int i = 0; i < n; i++){
            for (int j = i + 1; j < n; j++) {
                Object oI = list[i];
                Object oJ = list[j];

                if (stringer.getString(oI).compareTo(stringer.getString(oJ)) > 0) {
                    temp = list[i];
                    list[i] = list[j];
                    list[j] = temp;
                }
            }
        }
    }

    public interface Stringer {
        String getString(Object o);
    }
}
