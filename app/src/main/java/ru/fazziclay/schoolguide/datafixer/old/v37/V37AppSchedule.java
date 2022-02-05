package ru.fazziclay.schoolguide.datafixer.old.v37;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class V37AppSchedule extends V37Schedule {

    @SerializedName("selectedPreset")
    public UUID selectedPresetUUID = new UUID(0, 0);
}
