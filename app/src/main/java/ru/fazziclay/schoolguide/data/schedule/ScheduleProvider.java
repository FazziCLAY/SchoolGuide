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
    public static final int CURRENT_FORMAT_VERSION = 4;

    public static LocalSchedule copyLocalSchedule(LocalSchedule localSchedule) {
        Class<? extends LocalSchedule> l = LocalSchedule.class;
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(localSchedule, l), l);
    }

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
        save();
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
}
