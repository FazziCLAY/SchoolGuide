package ru.fazziclay.schoolguide.data.schedule.info;

import androidx.annotation.NonNull;

public class LessonInfo implements Cloneable {
    public short id;
    public short teacher;
    public String name;

    public LessonInfo(short id, short teacher, String name) {
        this.id = id;
        this.teacher = teacher;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s (%s)", name, id);
    }

    @NonNull
    public LessonInfo clone() {
        return new LessonInfo(this.id, this.teacher, this.name);
    }
}
