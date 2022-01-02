package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.UUID;

import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.util.UUIDUtil;

public class ScheduleData extends BaseData {
    public static ScheduleData load(Gson gson, String filePath) {
        return (ScheduleData) load(gson, filePath, ScheduleData.class);
    }

    HashMap<UUID, EventInfo> events = new HashMap<>();
    HashMap<UUID, SchedulePreset> presets = new HashMap<>();

    @NonNull
    @Override
    public String toString() {
        return "ScheduleData{" +
                "events=HashMap" + events +
                ", presets=HashMap" + presets +
                '}';
    }

    public UUID addEventInfo(EventInfo eventInfo) {
        UUID uuid = UUIDUtil.generateUUID(events.keySet().toArray(new UUID[0]));
        setEventInfo(uuid, eventInfo);
        return uuid;
    }

    public void setEventInfo(UUID uuid, EventInfo eventInfo) {
        events.put(uuid, eventInfo);
    }

    public UUID addSchedulePreset(SchedulePreset schedulePreset) {
        UUID uuid = UUIDUtil.generateUUID(presets.keySet().toArray(new UUID[0]));
        setSchedulePreset(uuid, schedulePreset);
        return uuid;
    }

    public void setSchedulePreset(UUID uuid, SchedulePreset schedulePreset) {
        presets.put(uuid, schedulePreset);
    }

    public EventInfo getEventInfo(UUID uuid) {
        return events.get(uuid);
    }

    public SchedulePreset getSchedulePreset(UUID uuid) {
        return presets.get(uuid);
    }
}
