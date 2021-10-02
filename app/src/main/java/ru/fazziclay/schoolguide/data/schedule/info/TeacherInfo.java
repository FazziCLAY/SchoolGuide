package ru.fazziclay.schoolguide.data.schedule.info;

import androidx.annotation.NonNull;

public class TeacherInfo implements Cloneable {
    public short id;
    public String name;

    public TeacherInfo(short id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s (%s)", name, id);
    }

    @NonNull
    public TeacherInfo clone() {
        return new TeacherInfo(this.id, this.name);
    }
}
