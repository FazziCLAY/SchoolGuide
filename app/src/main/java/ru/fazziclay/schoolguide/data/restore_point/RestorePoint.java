package ru.fazziclay.schoolguide.data.restore_point;

import androidx.annotation.NonNull;

import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.schedule.Schedule;

public class RestorePoint extends BaseData {
    public transient String fileName = null;
    public String name;
    public long createdTime;
    public Schedule schedule;

    public RestorePoint(String name,
                        long createdTime,
                        Schedule schedule) {
        this.name = name;
        this.createdTime = createdTime;
        this.schedule = schedule;
    }

    @NonNull
    @Override
    public String toString() {
        return "RestorePoint{" +
                "formatVersion=" + formatVersion +
                ", name='" + name + '\'' +
                ", createdTime=" + createdTime +
                ", schedule=" + schedule +
                '}';
    }
}
