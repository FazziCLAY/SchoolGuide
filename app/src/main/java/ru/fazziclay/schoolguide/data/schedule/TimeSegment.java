package ru.fazziclay.schoolguide.data.schedule;

public class TimeSegment {
    int start;    // in seconds
    int duration; // in seconds

    public TimeSegment(int start, int duration) {
        this.start = start;
        this.duration = duration;
    }

    public int getStart() {
        return start;
    }

    public int getDuration() {
        return duration;
    }

    public int getEnd() {
        return start + duration;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
