package ru.fazziclay.schoolguide.android.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.jsonparser.JsonDefaultStartTime;
import ru.fazziclay.schoolguide.data.jsonparser.JsonRootLesson;
import ru.fazziclay.schoolguide.data.jsonparser.JsonRoot;
import ru.fazziclay.schoolguide.data.jsonparser.JsonRootTeacher;
import ru.fazziclay.schoolguide.data.jsonparser.JsonWeek;
import ru.fazziclay.schoolguide.data.jsonparser.JsonWeekLesson;

public class MainActivity extends AppCompatActivity {
    Thread loadingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingThread = new Thread(this::loading);
        loadingThread.start();
    }

    public void loading() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        if (!FileUtil.isExist(JsonRoot.getSchoolFilePath(this))) {
            LinkedList<JsonRootTeacher> teachers = new LinkedList<>();
            teachers.add(new JsonRootTeacher(1, "Учитель 1"));
            teachers.add(new JsonRootTeacher(2, "Учитель 2"));

            LinkedList<JsonRootLesson> lessons = new LinkedList<>();
            lessons.add(new JsonRootLesson(1, 2, "Урок 1"));
            lessons.add(new JsonRootLesson(2, 1, "Урок 2"));

            LinkedList<JsonWeekLesson> exampleLesson = new LinkedList<>();
            exampleLesson.add(new JsonWeekLesson(2, -1, null));
            exampleLesson.add(new JsonWeekLesson(1, 20, "9:40"));

            JsonWeek week = new JsonWeek(
                    40,
                    new JsonDefaultStartTime(
                            "8:30",
                            "9:20",
                            "10:20",
                            "11:20",
                            "12:10",
                            "13:00",
                            "13:50",
                            "14:40",
                            "15:30",
                            "16:20"
                            ),
                    new LinkedList<>(),
                    exampleLesson,
                    new LinkedList<>(),
                    new LinkedList<>(),
                    new LinkedList<>(),
                    new LinkedList<>(),
                    new LinkedList<>());

            JsonRoot defaultSchool = new JsonRoot(teachers, lessons, week);

            FileUtil.write(JsonRoot.getSchoolFilePath(this), gson.toJson(defaultSchool, JsonRoot.class));
        }

        startService(new Intent(this, ForegroundService.class));
        startActivity(new Intent(this, HomeActivity.class));

        finish();
    }
}