package ru.fazziclay.schoolguide.data.jsonparser;

import android.content.Context;

import com.google.gson.Gson;

import java.util.LinkedList;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.data.SchoolDay;
import ru.fazziclay.schoolguide.data.SchoolLesson;
import ru.fazziclay.schoolguide.data.SchoolWeek;

public class JsonParser {
    public JsonRoot getJsonRoot(Context context) {
        Gson gson = new Gson();
        return gson.fromJson(FileUtil.read(JsonRoot.getSchoolFilePath(context)), JsonRoot.class);
    }

    public SchoolWeek parse(JsonRoot jsonRoot) {
        return new SchoolWeek(
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getMonday()),
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getTuesday()),
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getWednesday()),
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getThursday()),
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getFriday()),
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getSaturday()),
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getSunday())
        );
    }

    public SchoolDay getSchoolDayByJsonDay(JsonRoot jsonRoot, LinkedList<JsonWeekLesson> jsonWeekLessons) {
        LinkedList<SchoolLesson> dayLessons = new LinkedList<>();
        JsonDefaultStartTime defaultStartTime = jsonRoot.getWeek().getDefaultStartTime();

        int i = 1;
        for (JsonWeekLesson jsonWeekLesson : jsonWeekLessons) {
            JsonRootLesson jsonLesson = jsonRoot.getLessonByDayLesson(jsonWeekLesson);
            String name = jsonLesson.getName();
            String teacher = jsonRoot.getLessonTeacher(jsonLesson).getName();

            // Start time
            int startTime;
            if (jsonWeekLesson.isUseDefaultStartTime()) {
                startTime = defaultStartTime.toMillis(defaultStartTime.get(i));
            } else {
                startTime = defaultStartTime.toMillis(jsonWeekLesson.getStartTime());
            }

            // Duration
            int duration = jsonWeekLesson.getDuration();
            if (jsonWeekLesson.isUseDefaultDuration()) {
                duration = jsonRoot.getWeek().getDefaultDuration();
            }
            duration = duration * 60 * 1000;

            dayLessons.add(new SchoolLesson(name, teacher, startTime, duration));
            i++;
        }

        return new SchoolDay(dayLessons);
    }
}
