package ru.fazziclay.schoolguide.data.schedule;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.data.schedule.info.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.info.TeacherInfo;

public class ScheduleProvider {
    UUID instanceUUID;
    Schedule schedule;

    public ScheduleProvider(Context context) {
        instanceUUID = UUID.randomUUID();
        Gson gson = new Gson();
        this.schedule = gson.fromJson(FileUtil.read(Schedule.getScheduleFilePath(context), "{}"), Schedule.class);
    }

    public long getCurrentTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public static long getCurrentDayInSeconds() {
        Calendar calendar = new GregorianCalendar();
        Calendar currentDay = new GregorianCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        return currentDay.getTimeInMillis() / 1000;
    }

    /**
     * @return State of the day
     * **/
    public State getState() {
        if (getNowLesson() != null) {
            return State.SCHOOL_LESSON;
        } else {
            if (getNextLesson() == null) {
                return State.SCHOOL_END;
            } else {
                return State.SCHOOL_REST;
            }
        }
    }

    /**
     * @return время в секундах до начала урока
     * **/
    public long getLeftUntilLesson() {
        // Берём следующий урок и высчитываем время его начала
        // потом вычитаем из него текущее
        ScheduledLesson lesson = getNextLesson();
        if (lesson == null) return -1;
        return (getCurrentDayInSeconds() + lesson.startTime) - getCurrentTimeInSeconds();
    }

    /**
     * @return время до начала отдыха в секундах
     * **/
    public long getLeftUntilRest() {
        // Ищем текущий урок (=> если перемена то узнать время перемены не выйдет по этому даём 0 если null)
        // Берём и высчитываем время конца урока и из него вычитаем текущее время
        ScheduledLesson lesson = getNowLesson();
        if (lesson == null) return 0;
        //      (   (время конца урока)                  - (отнять)                   (Время текущее)      )
        return ((getCurrentDayInSeconds() + (lesson.startTime + lesson.duration)) - getCurrentTimeInSeconds());
    }

    /**
     * @return текущий урок. Если нету то null
     * **/
    public ScheduledLesson getNowLesson() {
        List<ScheduledLesson> list = getTodayLessons();
        int i = 0;
        while (i < list.size()) {
            ScheduledLesson q = list.get(i);
            if (isScheduledLessonNow(q)) return q;
            i++;
        }
        return null;
    }

    /**
     * @return следующий урок если нету то null
     * **/
    public ScheduledLesson getNextLesson() {
        List<ScheduledLesson> list = getTodayLessons();
        int i = 0;
        while (i < list.size()) {
            ScheduledLesson q = list.get(i);
            if (!isScheduledLessonStarted(q)) return q;
            i++;
        }
        return null;
    }

    /**
     * @return Список уроков на сегодня
     * **/
    public List<ScheduledLesson> getTodayLessons() {
        Calendar calendar = new GregorianCalendar();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.MONDAY) return schedule.week.monday;
        if (dayOfWeek == Calendar.TUESDAY) return schedule.week.tuesday;
        if (dayOfWeek == Calendar.WEDNESDAY) return schedule.week.wednesday;
        if (dayOfWeek == Calendar.THURSDAY) return schedule.week.thursday;
        if (dayOfWeek == Calendar.FRIDAY) return schedule.week.friday;
        if (dayOfWeek == Calendar.SATURDAY) return schedule.week.saturday;
        if (dayOfWeek == Calendar.SUNDAY) return schedule.week.sunday;

        throw new Error("Day of week not found! || День недели не найден");
    }

    /**
     * @param teacherInfoId искаемый id объекта
     * @return TeacherInfo по запрошеному teacherInfoId
     * **/
    public TeacherInfo getTeacherInfoById(short teacherInfoId) {
        List<TeacherInfo> list = schedule.teachers;
        int i = 0;
        while (i < list.size()) {
            TeacherInfo q = list.get(i);
            if (q.id == teacherInfoId) return q;
            i++;
        }
        return null;
    }

    /**
     * @param lessonInfoId искаемый id объекта
     * @return LessonInfo по запрошеному lessonInfoId
     * **/
    public LessonInfo getLessonInfoById(short lessonInfoId) {
        List<LessonInfo> list = schedule.lessons;
        int i = 0;
        while (i < list.size()) {
            LessonInfo q = list.get(i);
            if (q.id == lessonInfoId) return q;
            i++;
        }
        return null;
    }

    /**
     * @param lesson урок на проверку
     * @return начался ли уже урок (причём даже если уже закончился он всё равно начался(true))
     * **/
    public boolean isScheduledLessonStarted(ScheduledLesson lesson) {
        return getCurrentTimeInSeconds() >= (getCurrentDayInSeconds() + lesson.startTime);
    }

    /**
     * @param lesson урок на проверку
     * @return закончился ли урок
     * **/
    public boolean isScheduledLessonEnded(ScheduledLesson lesson) {
        return getCurrentTimeInSeconds() >= (getCurrentDayInSeconds() + lesson.startTime + lesson.duration);
    }

    /**
     * @param lesson урок на проверку
     * @return идёт ли этот урок прямо сейчас (начался и не закончился)
     * **/
    public boolean isScheduledLessonNow(ScheduledLesson lesson) {
        return (isScheduledLessonStarted(lesson) && !isScheduledLessonEnded(lesson));
    }

    public ScheduleWeek getScheduleWeek() {
        return schedule.week;
    }

    public List<TeacherInfo> getTeacherInfoList() {
        return schedule.teachers;
    }

    public List<LessonInfo> getLessonsInfoList() {
        return schedule.lessons;
    }

    public void save(String filePath) {
        schedule.save(filePath);
    }

    /**
     * @return status. 0 - successes; 1 - error
     * **/
    public byte removeTeacherInfo(TeacherInfo teacherInfo) {
        List<LessonInfo> list = schedule.lessons;
        int i = 0;
        while (i < list.size()) {
            LessonInfo q = list.get(i);
            if (q.teacher == teacherInfo.id) return 1;
            i++;
        }
        schedule.teachers.remove(teacherInfo);
        return 0;
    }

    public void addTeacherInfo(String name) {
        short id = 0;
        int i = 0;
        while (getTeacherInfoById(id) != null) {
            if (i > 1000) {
                throw new Error("add teacher i > 1000");
            }
            id = (short) new Random().nextInt(Short.MAX_VALUE + 1);
            i++;
        }

        TeacherInfo teacherInfo = new TeacherInfo(id, name);
        schedule.teachers.add(teacherInfo);
    }

    public void addLessonInfo(String name, short teacher) {
        short id = 0;
        int i = 0;
        while (getLessonInfoById(id) != null) {
            if (i > 1000) {
                throw new Error("add lesson i > 1000");
            }
            id = (short) new Random().nextInt(Short.MAX_VALUE + 1);
            i++;
        }

        LessonInfo teacherInfo = new LessonInfo(id, teacher, name);
        schedule.lessons.add(teacherInfo);
    }

    public byte removeLessonInfo(LessonInfo lessonInfo) {
        List<List<ScheduledLesson>> list = getScheduleWeek().getInList();
        int i = 0;
        while (i < list.size()) {
            List<ScheduledLesson> day = list.get(i);
            int i1 = 0;
            while (i1 < day.size()) {
                ScheduledLesson scheduledLesson = day.get(i1);
                if (getLessonInfoById(scheduledLesson.id).id == lessonInfo.id) return 1;
                i1++;
            }
            i++;
        }

        schedule.lessons.remove(lessonInfo);
        return 0;
    }

    public String scheduleLessonToString(ScheduledLesson scheduledLesson) {
        return String.format("%s (%s)",
                getLessonInfoById(scheduledLesson.id).name,
                getTeacherInfoById(getLessonInfoById(scheduledLesson.id).teacher).name);
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
