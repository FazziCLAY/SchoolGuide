package ru.fazziclay.schoolguide.data.schedule;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.data.DataBase;
import ru.fazziclay.schoolguide.data.schedule.info.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.info.TeacherInfo;

public class Schedule extends DataBase implements Cloneable {
    private static final String SCHEDULE_FILE = "schedule.json";

    public static String getScheduleFilePath(Context context) {
        return context.getExternalFilesDir("").getAbsolutePath() + "/" + SCHEDULE_FILE;
    }

    @SerializedName("version")
    public int formatVersion = 1;
    public List<TeacherInfo> teachers = new ArrayList<>();
    public List<LessonInfo> lessons = new ArrayList<>();
    public ScheduleWeek week = new ScheduleWeek();

    @Override
    public void save(String filePath) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        FileUtil.write(filePath, gson.toJson(this, Schedule.class));
    }

    @NonNull
    @Override
    public String toString() {
        return "Schedule{" +
                "formatVersion=" + formatVersion +
                ", teachers=" + teachers +
                ", lessons=" + lessons +
                ", week=" + week +
                '}';
    }

    @NonNull
    @Override
    public Schedule clone() {
        Schedule _schedule = new Schedule();
        List<TeacherInfo> _teachers = new ArrayList<>();
        List<LessonInfo> _lessons = new ArrayList<>();
        ScheduleWeek _week;

        // Teachers
        int i = 0;
        while (i < teachers.size()) {
            TeacherInfo teacherInfo = teachers.get(i);
            _teachers.add(teacherInfo.clone());
            i++;
        }

        // Lessons
        i = 0;
        while (i < lessons.size()) {
            LessonInfo lessonInfo = lessons.get(i);
            _lessons.add(lessonInfo.clone());
            i++;
        }

        _week = week.clone();

        _schedule.teachers = _teachers;
        _schedule.lessons = _lessons;
        _schedule.week = _week;
        return _schedule;
    }
}
