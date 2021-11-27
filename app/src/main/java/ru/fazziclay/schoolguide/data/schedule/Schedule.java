package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

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

    public Schedule copy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this, this.getClass()), this.getClass());
    }
}
