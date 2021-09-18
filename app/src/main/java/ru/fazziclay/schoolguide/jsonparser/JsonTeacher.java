package ru.fazziclay.schoolguide.jsonparser;

import androidx.annotation.NonNull;

public class JsonTeacher {
    int id;
    String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return "JsonTeacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
