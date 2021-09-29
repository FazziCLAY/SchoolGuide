package ru.fazziclay.schoolguide.data.jsonparser;

import androidx.annotation.NonNull;

public class JsonRootTeacher {
    int id;
    String name;

    public JsonRootTeacher(int id, String name) {
        this.id = id;
        this.name = name;
    }

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
