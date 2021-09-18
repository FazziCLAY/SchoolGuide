package ru.fazziclay.schoolguide.jsonparser;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;

public class JsonRoot {

    @SerializedName("version")
    int formatVersion = 1;
    LinkedList<JsonTeacher> teachers;
    LinkedList<JsonLesson> lessons;
    JsonWeek week;

    public LinkedList<JsonTeacher> getTeachers() {
        return teachers;
    }

    public LinkedList<JsonLesson> getLessons() {
        return lessons;
    }

    public JsonWeek getWeek() {
        return week;
    }

    public JsonLesson getLessonById(int lessonId) {
        for (JsonLesson lesson : getLessons()) {
            if (lesson.getId() == lessonId) return lesson;
        }
        return null;
    }

    public JsonTeacher getTeacherById(int teacherId) {
        for (JsonTeacher teacher : getTeachers()) {
            if (teacher.getId() == teacherId) return teacher;
        }
        return null;
    }

    public JsonTeacher getLessonTeacher(JsonLesson lesson) {
        return getTeacherById(lesson.getTeacher());
    }

    public JsonLesson getLessonByDayLesson(JsonDayLesson lesson) {
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
