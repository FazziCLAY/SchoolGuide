package ru.fazziclay.schoolguide.data.schedule;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.BaseProvider;
import ru.fazziclay.schoolguide.data.schedule.info.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.info.TeacherInfo;
import ru.fazziclay.schoolguide.datafixer.schedule.ScheduleDataFixer;

public class ScheduleProvider extends BaseProvider {
    private static final String SCHEDULE_FILE = "schedule.json";
    private static final int CURRENT_FORMAT_VERSION = 1;

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

    /**
     * @return State of the day
     * **/
    public State getState() {
        boolean isRestEnding = (getLeftUntilLesson() < 3 * 60);
        boolean isLessonEnding = (getLeftUntilRest() < 5 * 60);

        if (getNowLesson() != null) {
            if (isLessonEnding) return State.LESSON_ENDING;
            return State.LESSON;
        } else {
            if (getNextLesson() == null) {
                return State.END;
            } else {
                if (isRestEnding) return State.REST_ENDING;
                return State.REST;
            }
        }
    }

    /**
     * @return текущий урок. Если нету то null
     * **/
    public Lesson getNowLesson() {
        List<Lesson> list = getTodayLessons();
        int i = 0;
        while (i < list.size()) {
            Lesson q = list.get(i);
            if (isScheduledLessonNow(q)) return q;
            i++;
        }
        return null;
    }

    /**
     * @return следующий урок если нету то null
     * **/
    public Lesson getNextLesson() {
        List<Lesson> list = getTodayLessons();
        int i = 0;
        while (i < list.size()) {
            Lesson q = list.get(i);
            if (!isScheduledLessonStarted(q)) return q;
            i++;
        }
        return null;
    }

    /**
     * @return время в секундах до начала урока
     * **/
    public long getLeftUntilLesson() {
        Lesson lesson = getNextLesson();
        if (lesson == null) return 0;
        long a = (getCurrentDayInSeconds() + lesson.getTime().getStart()) - getCurrentTimeInSeconds();
        return (a < 0 ? 0 : a); // Если меньше нуля то вернём 0
    }

    /**
     * @return время до начала отдыха в секундах
     * **/
    public long getLeftUntilRest() {
        Lesson lesson = getNowLesson();
        if (lesson == null) return 0;
        long a = ((getCurrentDayInSeconds() + lesson.getTime().getEnd()) - getCurrentTimeInSeconds());
        return (a < 0 ? 0 : a); // Если меньше нуля то вернём 0
    }

    public void setSchedule(Schedule schedule) {
        this.data = schedule;
    }

    public Schedule getSchedule() {
        return ((Schedule) data);
    }

    /**
     * @return Список уроков на сегодня
     * **/
    public List<Lesson> getTodayLessons() {
        Schedule schedule = getSchedule();
        int dayOfWeek = getCurrentDayOfWeek();

        if (dayOfWeek == Calendar.MONDAY)    return schedule.monday;
        if (dayOfWeek == Calendar.TUESDAY)   return schedule.tuesday;
        if (dayOfWeek == Calendar.WEDNESDAY) return schedule.wednesday;
        if (dayOfWeek == Calendar.THURSDAY)  return schedule.thursday;
        if (dayOfWeek == Calendar.FRIDAY)    return schedule.friday;
        if (dayOfWeek == Calendar.SATURDAY)  return schedule.saturday;
        if (dayOfWeek == Calendar.SUNDAY)    return schedule.sunday;

        throw new Error("Day of week not found in schedule! dayOfWeek=="+dayOfWeek);
    }

    public TeacherInfo getTeacherInfoByUUID(UUID uuid) {
        return getSchedule().teachers.get(uuid);
    }

    public LessonInfo getLessonInfoByUUID(UUID uuid) {
        return getSchedule().lessons.get(uuid);
    }

    public void removeTeacherInfo(UUID uuid) {
        getSchedule().teachers.remove(uuid);
    }

    public UUID addTeacherInfo(TeacherInfo teacherInfo) {
        UUID uuid = UUID.randomUUID();
        getSchedule().teachers.put(uuid, teacherInfo);
        return uuid;
    }

    public UUID addLessonInfo(LessonInfo lessonInfo) {
        UUID uuid = UUID.randomUUID();
        getSchedule().lessons.put(uuid, lessonInfo);
        return uuid;
    }

    public UUID[] getAllTeachersUUID() {
        return getSchedule().teachers.keySet().toArray(new UUID[]{});
    }

    public UUID[] getAllLessonsUUID() {
        return getSchedule().lessons.keySet().toArray(new UUID[]{});
    }

    // ==============================================
    //  P R I V A T E
    // ==============================================
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

    private static int getCurrentDayOfWeek() {
        Calendar calendar = new GregorianCalendar();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * @param lesson урок на проверку
     * @return начался ли уже урок (причём даже если уже закончился он всё равно начался(true))
     * **/
    private boolean isScheduledLessonStarted(Lesson lesson) {
        return getCurrentTimeInSeconds() >= (getCurrentDayInSeconds() + lesson.getTime().getStart());
    }

    /**
     * @param lesson урок на проверку
     * @return закончился ли урок
     * **/
    private boolean isScheduledLessonEnded(Lesson lesson) {
        return getCurrentTimeInSeconds() >= (getCurrentDayInSeconds() + lesson.getTime().getEnd());
    }

    /**
     * @param lesson урок на проверку
     * @return идёт ли этот урок прямо сейчас (начался и не закончился)
     * **/
    private boolean isScheduledLessonNow(Lesson lesson) {
        return (isScheduledLessonStarted(lesson) && !isScheduledLessonEnded(lesson));
    }
}
