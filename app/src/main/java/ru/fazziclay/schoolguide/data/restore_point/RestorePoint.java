package ru.fazziclay.schoolguide.data.restore_point;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import ru.fazziclay.schoolguide.data.schedule.Schedule;

public class RestorePoint {
    public transient String fileName = null;
    public int version = 1;
    public String name = null;
    public long createdTime = 0;
    public Schedule schedule = new Schedule();

    public RestorePoint(String name,
                        long createdTime,
                        Schedule schedule) {
        this.name = name;
        this.createdTime = createdTime;
        this.schedule = schedule;
    }

    public RestorePoint() {}

    @NonNull
    @Override
    public String toString() {
        return "RestorePoint{" +
                "version=" + version +
                ", name='" + name + '\'' +
                ", createdTime=" + createdTime +
                ", schedule=" + schedule +
                '}';
    }
}
