package ru.fazziclay.schoolguide.jsonparser;

import androidx.annotation.NonNull;

public class JsonLesson {
    int id;
    String name;
    int teacher;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTeacher() {
        return teacher;
    }

    @NonNull
    @Override
    public String toString() {
        return "JsonLesson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teacher='" + teacher + '\'' +
                '}';
    }
}
