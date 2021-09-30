package ru.fazziclay.schoolguide.data.schedule.info;

import androidx.annotation.NonNull;

public class LessonInfo {
    public short id;
    public short teacher;
    public String name;

    @NonNull
    @Override
    public String toString() {
        return String.format("%s (%s)", name, id);
    }
}
