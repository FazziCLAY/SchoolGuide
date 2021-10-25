package ru.fazziclay.schoolguide.data.schedule;

import android.content.Context;
import com.google.gson.Gson;
import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.BaseProvider;
import ru.fazziclay.schoolguide.datafixer.schedule.ScheduleDataFixer;
import ru.fazziclay.schoolguide.util.FileUtil;

import java.util.*;

public class ScheduleProvider extends BaseProvider {
    private static final String SCHEDULE_FILE = "schedule.json";
    private static final int CURRENT_FORMAT_VERSION = 2;

    public ScheduleProvider(Context context) {
        filePath = context.getExternalFilesDir(null).getAbsolutePath().concat("/").concat(SCHEDULE_FILE);
        data = load();
        if (data.isFormatVersionDefault()) data.formatVersion = CURRENT_FORMAT_VERSION;

        save();
    }

    @Override
    public BaseData load() {
        Gson gson = new Gson();
        ScheduleDataFixer dataFixer = new ScheduleDataFixer();
        String fileContent = FileUtil.read(filePath, "{}");
        return gson.fromJson(dataFixer.fix(fileContent), Schedule.class);
    }

    public Schedule getSchedule() {
        return (Schedule) data;
    }

    public void setSchedule(Schedule schedule) {
        data = schedule;
    }

    // Получить UUID всех информаций уроков
    public UUID[] getAllLessons() {
        return getSchedule().lessons.keySet().toArray(new UUID[0]);
    }

    // Получить UUID всех локальных расписаний
    public UUID[] getAllSchedules() {
        return getSchedule().schedules.keySet().toArray(new UUID[0]);
    }

    // Получить информацию урока по его UUID
    public LessonInfo getLessonInfo(UUID lessonUUID) {
        return getSchedule().lessons.get(lessonUUID);
    }

    // Добавить информацию об уроке и вернуть его UUID
    public UUID addLessonInfo(LessonInfo lessonInfo) {
        UUID newUUID = null;
        boolean a = true;
        while (a) {
            newUUID = UUID.randomUUID();
            if (!getSchedule().lessons.containsKey(newUUID)) a = false;
            getSchedule().lessons.put(newUUID, lessonInfo);
        }
        save();
        return newUUID;
    }

    // Удалить информацию об уроке по его UUID
    public void removeLessonInfo(UUID lessonUUID) {
        getSchedule().lessons.remove(lessonUUID);
        save();
    }

    // Получить локальное расписание по его UUID
    public LocalSchedule getLocalSchedule(UUID scheduleUUID) {
        return getSchedule().schedules.get(scheduleUUID);
    }

    // Добавить локальное расписание и вернуть его UUID
    public UUID addLocalSchedule(LocalSchedule localSchedule) {
        UUID newUUID = null;
        boolean a = true;
        while (a) {
            newUUID = UUID.randomUUID();
            if (!getSchedule().schedules.containsKey(newUUID)) a = false;
            getSchedule().schedules.put(newUUID, localSchedule);
        }
        save();
        return newUUID;
    }

    // Удалить локальное расписание по его UUID
    public void removeLocalSchedule(UUID scheduleUUID) {
        getSchedule().schedules.remove(scheduleUUID);
        save();
    }

    public List<Lesson> getToday(UUID localScheduleUUID) {
        LocalSchedule localSchedule = getLocalSchedule(localScheduleUUID);
        if (localSchedule == null) {
            List<Lesson> a = new ArrayList<>();
            a.add(new Lesson(new UUID(0, 0), 0, 0));
            return a;
        }
        return localSchedule.get(getCurrentDayOfWeek());
    }

    public Lesson getNowLesson(UUID localScheduleUUID) {
        List<Lesson> nowLessons = new ArrayList<>();
        for (Lesson lesson : getToday(localScheduleUUID)) {
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

    public Lesson getNextLesson(UUID localScheduleUUID) {
        List<Lesson> nextLessons = new ArrayList<>();
        for (Lesson lesson : getToday(localScheduleUUID)) {
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

    public int getTimeBeforeStartLesson(UUID localScheduleUUID) {
        Lesson nextLesson = getNextLesson(localScheduleUUID);
        if (nextLesson == null) return 0;
        long globalStart = getCurrentDayInSeconds() + nextLesson.getStart();
        return (int) (globalStart - getCurrentTimeInSeconds());
    }
    // До начала перемены осталось

    public int getTimeBeforeStartRest(UUID localScheduleUUID) {
        Lesson nowLesson = getNowLesson(localScheduleUUID);
        if (nowLesson == null) return 0;
        long globalEnd = getCurrentDayInSeconds() + nowLesson.getEnd();
        return (int) (globalEnd - getCurrentTimeInSeconds());
    }

    public State getState(UUID localScheduleUUID) {
        if (getNowLesson(localScheduleUUID) != null) {
            return State.LESSON
                    .setEnding(getTimeBeforeStartRest(localScheduleUUID) < 5);
        } else if (getNextLesson(localScheduleUUID) != null) {
            return State.REST
                    .setEnding(getTimeBeforeStartLesson(localScheduleUUID) < 3);
        } else {
            return State.END;
        }
    }
    // ===================================
    // P  R  I  V  A  T  E      Z  O  N  E
    // ===================================

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
