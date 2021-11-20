package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class LocalSchedule {
    String name = "Unknown";

    List<Lesson> monday    = new ArrayList<>();
    List<Lesson> tuesday   = new ArrayList<>();
    List<Lesson> wednesday = new ArrayList<>();
    List<Lesson> thursday  = new ArrayList<>();
    List<Lesson> friday    = new ArrayList<>();
    List<Lesson> saturday  = new ArrayList<>();
    List<Lesson> sunday    = new ArrayList<>();

    public LocalSchedule(String name) {
        this.name = name;
    }

    public List<Lesson> get(int dayOfWeek) {
        if (dayOfWeek == Calendar.MONDAY)    return monday;
        if (dayOfWeek == Calendar.TUESDAY)   return tuesday;
        if (dayOfWeek == Calendar.WEDNESDAY) return wednesday;
        if (dayOfWeek == Calendar.THURSDAY)  return thursday;
        if (dayOfWeek == Calendar.FRIDAY)    return friday;
        if (dayOfWeek == Calendar.SATURDAY)  return saturday;
        if (dayOfWeek == Calendar.SUNDAY)    return sunday;
        throw new Error("dayOfWeek("+dayOfWeek+") is not found!");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Lesson> getToday() {
        return this.get(getCurrentDayOfWeek());
    }

    public Lesson getNowLesson() {
        List<Lesson> nowLessons = new ArrayList<>();
        for (Lesson lesson : getToday()) {
            if (isScheduledLessonNow(lesson)) nowLessons.add(lesson);
        }

        Lesson result = null;
        int startTime = 24*60*60;
        for (Lesson lesson : nowLessons) {
            if (startTime > lesson.getStart()) {
                startTime = lesson.getStart();
                result = lesson;
            }
        }
        return result;
    }

    public Lesson getNextLesson() {
        List<Lesson> nextLessons = new ArrayList<>();
        for (Lesson lesson : getToday()) {
            if (!isScheduledLessonStarted(lesson)) nextLessons.add(lesson);
        }

        Lesson result = null;
        int startTime = 24*60*60;
        for (Lesson lesson : nextLessons) {
            if (startTime > lesson.getStart()) {
                startTime = lesson.getStart();
                result = lesson;
            }
        }

        return result;
    }
    // До начала урока осталось

    public int getTimeBeforeStartLesson() {
        Lesson nextLesson = getNextLesson();
        if (nextLesson == null) return 0;
        long globalStart = getCurrentDayInSeconds() + nextLesson.getStart();
        return (int) (globalStart - getCurrentTimeInSeconds());
    }
    // До начала перемены осталось

    public int getTimeBeforeStartRest() {
        Lesson nowLesson = getNowLesson();
        if (nowLesson == null) return 0;
        long globalEnd = getCurrentDayInSeconds() + nowLesson.getEnd();
        return (int) (globalEnd - getCurrentTimeInSeconds());
    }

    public State getState() {
        if (getNowLesson() != null) {
            return State.LESSON
                    .setEnding(getTimeBeforeStartRest() < 5*60);
        } else if (getNextLesson() != null) {
            return State.REST
                    .setEnding(getTimeBeforeStartLesson() < 2*60);
        } else {
            return State.END;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "LocalSchedule{" +
                "name='" + name + '\'' +
                ", monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", sunday=" + sunday +
                '}';
    }

    private static int getCurrentDayOfWeek() {
        Calendar calendar = new GregorianCalendar();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private long getCurrentTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    private static long getCurrentDayInSeconds() {
        Calendar calendar = new GregorianCalendar();
        Calendar currentDay = new GregorianCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        return currentDay.getTimeInMillis() / 1000;
    }

    /**
     * @param lesson урок на проверку
     * @return начался ли уже урок (причём даже если уже закончился он всё равно начался(true))
     * **/
    private boolean isScheduledLessonStarted(Lesson lesson) {
        return getCurrentTimeInSeconds() >= (getCurrentDayInSeconds() + lesson.getStart());
    }

    /**
     * @param lesson урок на проверку
     * @return закончился ли урок
     * **/
    private boolean isScheduledLessonEnded(Lesson lesson) {
        return getCurrentTimeInSeconds() >= (getCurrentDayInSeconds() + lesson.getEnd());
    }

    /**
     * @param lesson урок на проверку
     * @return идёт ли этот урок прямо сейчас (начался и не закончился)
     * **/
    private boolean isScheduledLessonNow(Lesson lesson) {
        return (isScheduledLessonStarted(lesson) && !isScheduledLessonEnded(lesson));
    }
}
