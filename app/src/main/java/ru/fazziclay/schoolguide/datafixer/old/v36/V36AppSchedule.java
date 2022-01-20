package ru.fazziclay.schoolguide.datafixer.old.v36;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class V36AppSchedule extends V36Schedule {

    @SerializedName("currentPreset")
    public UUID currentPresetUUID = new UUID(0, 0);
}
