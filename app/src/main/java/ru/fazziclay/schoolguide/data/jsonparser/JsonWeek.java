package ru.fazziclay.schoolguide.data.jsonparser;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;

public class JsonWeek {
    int defaultDuration;
    JsonDefaultStartTime defaultStartTime;

    LinkedList<JsonWeekLesson> monday = new LinkedList<>();
    LinkedList<JsonWeekLesson> tuesday = new LinkedList<>();
    LinkedList<JsonWeekLesson> wednesday = new LinkedList<>();
    LinkedList<JsonWeekLesson> thursday = new LinkedList<>();
    LinkedList<JsonWeekLesson> friday = new LinkedList<>();
    LinkedList<JsonWeekLesson> saturday = new LinkedList<>();
    LinkedList<JsonWeekLesson> sunday = new LinkedList<>();

    public JsonWeek(int defaultDuration,
                    JsonDefaultStartTime defaultStartTime,
                    LinkedList<JsonWeekLesson> monday,
                    LinkedList<JsonWeekLesson> tuesday,
                    LinkedList<JsonWeekLesson> wednesday,
                    LinkedList<JsonWeekLesson> thursday,
                    LinkedList<JsonWeekLesson> friday,
                    LinkedList<JsonWeekLesson> saturday,
                    LinkedList<JsonWeekLesson> sunday) {
        this.defaultDuration = defaultDuration;
        this.defaultStartTime = defaultStartTime;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public JsonWeek() {}

    public int getDefaultDuration() {
        return defaultDuration;
    }

    public JsonDefaultStartTime getDefaultStartTime() {
        return defaultStartTime;
    }

    public LinkedList<JsonWeekLesson> getMonday() {
        return monday;
    }

    public LinkedList<JsonWeekLesson> getTuesday() {
        return tuesday;
    }

    public LinkedList<JsonWeekLesson> getWednesday() {
        return wednesday;
    }

    public LinkedList<JsonWeekLesson> getThursday() {
        return thursday;
    }

    public LinkedList<JsonWeekLesson> getFriday() {
        return friday;
    }

    public LinkedList<JsonWeekLesson> getSaturday() {
        return saturday;
    }

    public LinkedList<JsonWeekLesson> getSunday() {
        return sunday;
    }

    @NonNull
    @Override
    public String toString() {
        return "JsonWeek{" +
                "defaultDuration=" + defaultDuration +
                ", defaultStartTime=" + defaultStartTime +
                ", monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", sunday=" + sunday +
                '}';
    }
}
