package ru.fazziclay.schoolguide.data.schedule.info;

public class LessonInfo {
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

    public static LessonInfo createNone() {
        return new LessonInfo("[BUG] Gaming");
    }
}
