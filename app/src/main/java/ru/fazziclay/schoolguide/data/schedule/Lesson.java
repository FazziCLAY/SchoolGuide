package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Lesson extends TimeSegment {
    @SerializedName("lesson")
    private UUID lesson;

    public Lesson(UUID lesson, int start, int duration) {
        super(start, duration);
        this.lesson = lesson;
    }

    public UUID getLessonInfo() {
        return lesson;
    }

    public void setLessonInfo(UUID lesson) {
        this.lesson = lesson;
    }

    @NonNull
    @Override
    public String toString() {
        return "Lesson{" +
                "lesson=" + lesson +
                ", start=" + start +
                ", duration=" + duration +
                '}';
    }
}
