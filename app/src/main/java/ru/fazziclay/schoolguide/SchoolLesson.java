package ru.fazziclay.schoolguide;

import androidx.annotation.NonNull;

public class SchoolLesson {
    int startTime;
    int duration;

    String name;
    String teacher;

    public SchoolLesson(String name, String teacher, int startTime, int duration) {
        this.name = name;
        this.teacher = teacher;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getTeacher() {
        return teacher;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return startTime + duration;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isStarted() {
        return Clock.getCurrentTimeInMillis() >= Clock.secondsToDaySeconds(getStartTime());
    }

    public boolean isEnded() {
        return Clock.getCurrentTimeInMillis() > Clock.secondsToDaySeconds(getEndTime());
    }

    public boolean isNow() {
        return (isStarted() && !isEnded());
    }

    @NonNull
    @Override
    public String toString() {
        return name + "(" + teacher + ")";
    }
}
