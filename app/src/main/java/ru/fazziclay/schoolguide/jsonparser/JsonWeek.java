package ru.fazziclay.schoolguide.jsonparser;

import androidx.annotation.NonNull;

import java.util.LinkedList;

public class JsonWeek {
    int defaultDuration;
    JsonDefaultStartTime defaultStartTime;

    LinkedList<JsonDayLesson> monday;
    LinkedList<JsonDayLesson> tuesday;
    LinkedList<JsonDayLesson> wednesday;
    LinkedList<JsonDayLesson> thursday;
    LinkedList<JsonDayLesson> friday;

    public LinkedList<LinkedList<JsonDayLesson>> getList() {
        LinkedList<LinkedList<JsonDayLesson>> linkedLists = new LinkedList<>();
        linkedLists.add(monday);
        linkedLists.add(tuesday);
        linkedLists.add(wednesday);
        linkedLists.add(thursday);
        linkedLists.add(friday);
        return linkedLists;
    }

    public int getDefaultDuration() {
        return defaultDuration;
    }

    public JsonDefaultStartTime getDefaultStartTime() {
        return defaultStartTime;
    }

    public LinkedList<JsonDayLesson> getMonday() {
        return monday;
    }

    public LinkedList<JsonDayLesson> getTuesday() {
        return tuesday;
    }

    public LinkedList<JsonDayLesson> getWednesday() {
        return wednesday;
    }

    public LinkedList<JsonDayLesson> getThursday() {
        return thursday;
    }

    public LinkedList<JsonDayLesson> getFriday() {
        return friday;
    }

    @NonNull
    @Override
    public String toString() {
        return "JsonWeek{" +
                "monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                '}';
    }
}
