package ru.fazziclay.schoolguide.datafixer.old.v33;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class V33Lesson extends V33TimeSegment {
    @SerializedName("lesson")
    public UUID lesson;

    public V33Lesson(UUID lesson, int start, int duration) {
        super(start, duration);
        this.lesson = lesson;
    }
}
