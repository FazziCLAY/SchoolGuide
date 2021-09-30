package ru.fazziclay.schoolguide.data.schedule.info;

import androidx.annotation.NonNull;

public class TeacherInfo {
    public short id;
    public String name;

    @NonNull
    @Override
    public String toString() {
        return String.format("%s (%s)", name, id);
    }
}
