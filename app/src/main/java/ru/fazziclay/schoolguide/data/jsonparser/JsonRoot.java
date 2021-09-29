package ru.fazziclay.schoolguide.data.jsonparser;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;

import ru.fazziclay.schoolguide.data.SchoolDay;
import ru.fazziclay.schoolguide.data.SchoolLesson;
import ru.fazziclay.schoolguide.data.SchoolWeek;

public class JsonRoot {
    public final static String SCHOOL_FILE = "school.json";

    @SerializedName("version")
    int formatVersion = 1;
    LinkedList<JsonRootTeacher> teachers;
    LinkedList<JsonRootLesson> lessons;
    JsonWeek week;

    public JsonRoot(LinkedList<JsonRootTeacher> teachers, LinkedList<JsonRootLesson> lessons, JsonWeek week) {
        this.teachers = teachers;
        this.lessons = lessons;
        this.week = week;
    }

    public static String getSchoolFilePath(Context context) {
        return context.getExternalFilesDir("").getAbsoluteFile() + "/" + SCHOOL_FILE;
    }

    public LinkedList<JsonRootTeacher> getTeachers() {
        return teachers;
    }

    public LinkedList<JsonRootLesson> getLessons() {
        return lessons;
    }

    public JsonWeek getWeek() {
        return week;
    }

    public JsonRootLesson getLessonById(int lessonId) {
        for (JsonRootLesson lesson : getLessons()) {
            if (lesson.getId() == lessonId) return lesson;
        }
        return null;
    }

    public JsonRootTeacher getTeacherById(int teacherId) {
        for (JsonRootTeacher teacher : getTeachers()) {
            if (teacher.getId() == teacherId) return teacher;
        }
        return null;
    }

    public JsonRootTeacher getLessonTeacher(JsonRootLesson lesson) {
        return getTeacherById(lesson.getTeacher());
    }

    public JsonRootLesson getLessonByDayLesson(JsonWeekLesson lesson) {
        return getLessonById(lesson.getId());
    }

    @NonNull
    @Override
    public String toString() {
        return "JsonRoot{" +
                "formatVersion=" + formatVersion +
                ", lessons=" + lessons +
                ", week=" + week +
                '}';
    }
}
