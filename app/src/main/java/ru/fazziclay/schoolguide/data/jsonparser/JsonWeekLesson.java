package ru.fazziclay.schoolguide.data.jsonparser;

import androidx.annotation.NonNull;

public class JsonWeekLesson {
    int id;
    int duration;
    String startTime;

    public JsonWeekLesson(int id, int duration, String startTime) {
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
    }

    public boolean isUseDefaultDuration() {
        return (duration == -1);
    }

    public boolean isUseDefaultStartTime() {
        return (startTime == null);
    }

    public int getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    @NonNull
    @Override
    public String toString() {
        return "JsonDayLesson{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
