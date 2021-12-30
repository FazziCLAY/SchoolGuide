package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class LessonInfo {
    @SerializedName("name")
    private String name;

    public LessonInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "LessonInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
