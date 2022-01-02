package ru.fazziclay.schoolguide.data.schedule;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.UUID;

import ru.fazziclay.schoolguide.data.BaseData;

public class Schedule extends BaseData {
    private static final String SCHEDULE_FILE = "schoolguide.schedule.json";
    private static final int CURRENT_FORMAT_VERSION = 6;

    public static Schedule load(Context context) {
        Schedule schedule = (Schedule) BaseData.load(context.getExternalFilesDir(null).getAbsolutePath().concat("/").concat(SCHEDULE_FILE), Schedule.class);
        schedule.formatVersion = CURRENT_FORMAT_VERSION;
        schedule.save();
        return schedule;
    }

    @SerializedName("lessons")
    public HashMap<UUID, LessonInfo>    lessons   = new HashMap<>();
    @SerializedName("schedules")
    public HashMap<UUID, LocalSchedule> schedules = new HashMap<>();
}
