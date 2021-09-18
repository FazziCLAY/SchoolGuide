package ru.fazziclay.schoolguide;

import java.util.LinkedList;

public class SchoolDay {
    LinkedList<SchoolLesson> lessons;

    public SchoolDay(LinkedList<SchoolLesson> lessons) {
        this.lessons = lessons;
    }

    public LinkedList<SchoolLesson> getLessons() {
        return lessons;
    }

    public SchoolDayState getState() {
        if (getNowLesson() != null) {
            return SchoolDayState.SCHOOL_LESSON;
        } else {
            if (getNextLesson() == null) {
                return SchoolDayState.SCHOOL_END;
            } else {
                return SchoolDayState.SCHOOL_REST;
            }
        }
    }

    // Текущий урок
    public SchoolLesson getNowLesson() {
        int i = 0;
        while (i < lessons.size()) {
            SchoolLesson lesson = lessons.get(i);
            if (lesson.isNow()) return lesson;
            i++;
        }

        return null;
    }

    // Осталось до урока
    public long getLeftUntilLesson() {
        int i = 0;
        while (i < lessons.size()) {
            SchoolLesson lesson = lessons.get(i);
            if (!lesson.isStarted()) return (Clock.secondsToDaySeconds(lesson.getStartTime()) - Clock.getCurrentTimeInMillis());
            i++;
        }

        return 0;
    }

    // Осталось до перемены
    public long getLeftUntilRest() {
        if (getNowLesson() == null) return -100;
        return (Clock.secondsToDaySeconds(getNowLesson().getEndTime()) - Clock.getCurrentTimeInMillis());
    }

    // Сдедующий урок
    public SchoolLesson getNextLesson() {
        int i = 0;
        while (i < lessons.size()) {
            SchoolLesson lesson = lessons.get(i);
            if (!lesson.isStarted()) return lesson;
            i++;
        }

        return null;
    }
}
