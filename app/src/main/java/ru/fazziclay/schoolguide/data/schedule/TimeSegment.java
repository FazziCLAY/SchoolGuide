package ru.fazziclay.schoolguide.data.schedule;

public class TimeSegment {
    private int startTime;
    private int endTime;

    public TimeSegment(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getDuration() {
        return endTime - startTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
