package ru.fazziclay.schoolguide.data.schedule;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.data.DataBase;
import ru.fazziclay.schoolguide.data.schedule.info.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.info.TeacherInfo;
import ru.fazziclay.schoolguide.data.settings.Settings;

public class ScheduleData extends DataBase {
    public static final String SCHEDULE_FILE = "schedule.json";

    public static String getScheduleFilePath(Context context) {
        return context.getExternalFilesDir("").getAbsolutePath() + "/" + SCHEDULE_FILE;
    }

    @SerializedName("version")
    int formatVersion = 1;
    List<TeacherInfo> teachers = new ArrayList<>();
    List<LessonInfo> lessons = new ArrayList<>();
    ScheduleWeek week = new ScheduleWeek();

    @Override
    public void save(String filePath) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        FileUtil.write(filePath, gson.toJson(this, ScheduleData.class));
    }
}
