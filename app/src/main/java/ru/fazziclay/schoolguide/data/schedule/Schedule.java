package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;
import ru.fazziclay.schoolguide.data.BaseData;

import java.util.HashMap;
import java.util.UUID;

public class Schedule extends BaseData {
    public HashMap<UUID, LessonInfo>    lessons   = new HashMap<>();
    public HashMap<UUID, LocalSchedule> schedules = new HashMap<>();

    @NonNull
    @Override
    public String toString() {
        return "Schedule{" +
                "formatVersion=" + formatVersion +
                ", lessons=" + lessons +
                ", schedules=" + schedules +
                '}';
    }
}
