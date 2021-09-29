package ru.fazziclay.schoolguide.data.jsonparser;

import androidx.annotation.NonNull;

public class JsonRootLesson {
    int id;
    int teacher;
    String name;

    public JsonRootLesson(int id, int teacher, String name) {
        this.id = id;
        this.teacher = teacher;
        this.name = name;
    }

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
