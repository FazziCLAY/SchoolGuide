package ru.fazziclay.schoolguide.app.scheduleinformator;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Schedule;

public class AppSchedule extends Schedule {

    @SerializedName("currentPreset")
    public UUID currentPresetUUID = new UUID(0, 0);
}
