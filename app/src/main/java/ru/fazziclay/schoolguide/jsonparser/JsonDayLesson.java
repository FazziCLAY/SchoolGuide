package ru.fazziclay.schoolguide.jsonparser;

import androidx.annotation.NonNull;

public class JsonDayLesson {
    int id;
    String startTime;
    int duration;

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
