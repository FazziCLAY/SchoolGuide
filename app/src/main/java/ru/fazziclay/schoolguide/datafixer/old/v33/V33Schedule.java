package ru.fazziclay.schoolguide.datafixer.old.v33;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.UUID;

public class V33Schedule {
    @SerializedName("lessons")
    public HashMap<UUID, V33LessonInfo>    lessons   = new HashMap<>();
    @SerializedName("schedules")
    public HashMap<UUID, V33LocalSchedule> schedules = new HashMap<>();
}
