package ru.fazziclay.schoolguide.data.schedule;

import ru.fazziclay.schoolguide.data.schedule.info.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.info.TeacherInfo;

import java.util.UUID;

public class Lesson {
    private UUID lessonInfo  = null;
    private UUID teacherInfo = null;
    private TimeSegment time = null;

    public LessonInfo getLessonInfo() {
        return null;
    }

    public TeacherInfo getTeacherInfo() {
        return null;
    }

    public TimeSegment getTime() {
        return time;
    }

    public void setLessonInfo(LessonInfo lessonInfo) {
        this.lessonInfo = null;
        throw new Error("DEPRECATED FUNCTION CALLED!");
    }

    public void setTeacherInfo(TeacherInfo teacherInfo) {
        this.teacherInfo = null;
        throw new Error("DEPRECATED FUNCTION CALLED!");
    }

    public void setTime(TimeSegment time) {
        this.time = time;
    }
}
