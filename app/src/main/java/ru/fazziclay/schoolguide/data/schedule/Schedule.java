package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import ru.fazziclay.schoolguide.data.BaseData;

import java.util.HashMap;
import java.util.UUID;

public class Schedule extends BaseData {
    @SerializedName("lessons")
    public HashMap<UUID, LessonInfo>    lessons   = new HashMap<>();
    @SerializedName("schedules")
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
