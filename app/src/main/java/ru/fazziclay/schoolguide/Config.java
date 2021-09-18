package ru.fazziclay.schoolguide;

import android.content.Context;

import com.google.gson.Gson;

import java.util.LinkedList;

import ru.fazziclay.fazziclaylibs.FileUtils;
import ru.fazziclay.schoolguide.jsonparser.JsonDayLesson;
import ru.fazziclay.schoolguide.jsonparser.JsonDefaultStartTime;
import ru.fazziclay.schoolguide.jsonparser.JsonLesson;
import ru.fazziclay.schoolguide.jsonparser.JsonRoot;

public class Config {
    public static Config config;

    public static Config getConfig() {
        return config;
    }

    public static SchoolDay getSchoolDayByJsonDay(JsonRoot jsonRoot, LinkedList<JsonDayLesson> jsonDayLessons) {
        LinkedList<SchoolLesson> dayLessons = new LinkedList<>();
        JsonDefaultStartTime defaultStartTime = jsonRoot.getWeek().getDefaultStartTime();

        int i = 1;
        for (JsonDayLesson jsonDayLesson : jsonDayLessons) {
            JsonLesson jsonLesson = jsonRoot.getLessonByDayLesson(jsonDayLesson);
            String name = jsonLesson.getName();
            String teacher = jsonRoot.getLessonTeacher(jsonLesson).getName();

            // Start time
            int startTime;
            if (jsonDayLesson.isUseDefaultStartTime()) {
                startTime = defaultStartTime.toMillis(defaultStartTime.get(i));
            } else {
                startTime = defaultStartTime.toMillis(jsonDayLesson.getStartTime());
            }

            // Duration
            int duration = jsonDayLesson.getDuration();
            if (jsonDayLesson.isUseDefaultDuration()) {
                duration = jsonRoot.getWeek().getDefaultDuration();
            }
            duration = duration * 60 * 1000;

            dayLessons.add(new SchoolLesson(name, teacher, startTime, duration));
            i++;
        }

        return new SchoolDay(dayLessons);
    }

    public static void init(Context context) {
        config = new Config();

        String path = context.getExternalFilesDir("").getAbsolutePath() + "/school.json";
        Gson gson = new Gson();
        JsonRoot jsonRoot = gson.fromJson(FileUtils.read(path), JsonRoot.class);

        config.schoolWeek = new SchoolWeek(
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getMonday()),
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getTuesday()),
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getWednesday()),
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getThursday()),
                getSchoolDayByJsonDay(jsonRoot, jsonRoot.getWeek().getFriday())
        );
    }

    SchoolWeek schoolWeek;
}
