package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import ru.fazziclay.schoolguide.data.settings.Settings;

public class LocalSchedule {
    @SerializedName("name")
    public String name;

    @SerializedName("lessons")
    public List<Lesson> lessons = new ArrayList<>();

    public LocalSchedule(String name) {
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

    public State getState(Settings settingsProvider) {
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

    public LocalSchedule copy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this, this.getClass()), this.getClass());
    }
}
