package ru.fazziclay.schoolguide.data.schedule.info;

public class TeacherInfo implements Cloneable {
    private String name;

    public TeacherInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TeacherInfo createNone() {
        return new TeacherInfo("[BUG] Blue Bear");
    }
}
