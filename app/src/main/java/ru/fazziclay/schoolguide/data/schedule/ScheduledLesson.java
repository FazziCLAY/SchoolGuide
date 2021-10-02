package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

public class ScheduledLesson implements Cloneable {
    public short id;
    public int startTime;
    public int duration;

    public ScheduledLesson(short id, int startTime, int duration) {
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    @NonNull
    @Override
    public String toString() {
        return "ScheduledLesson{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    @NonNull
    public ScheduledLesson clone() {
        return new ScheduledLesson(this.id, this.startTime, this.duration);
    }
}
