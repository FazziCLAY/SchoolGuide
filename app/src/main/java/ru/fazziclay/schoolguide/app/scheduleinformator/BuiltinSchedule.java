package ru.fazziclay.schoolguide.app.scheduleinformator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Schedule;

public class BuiltinSchedule extends Schedule {
    List<UUID> autoSyncPresets = new ArrayList<>();

    public BuiltinSchedule() {

    }

    public List<UUID> getAutoSyncPresetsUUIDs() {
        return autoSyncPresets;
    }
}
