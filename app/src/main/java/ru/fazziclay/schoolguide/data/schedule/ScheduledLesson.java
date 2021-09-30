package ru.fazziclay.schoolguide.data.schedule;

public class ScheduledLesson {
    public short id;
    public int startTime;
    public int duration;

    public ScheduledLesson(short id, int startTime, int duration) {
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }
}
