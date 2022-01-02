package ru.fazziclay.schoolguide.data.schedule;

import com.google.gson.annotations.SerializedName;

public class LessonInfo {
    @SerializedName("name")
    public String name;

    public LessonInfo(String name) {
        this.name = name;
    }
}
