package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

public class EventInfo {
    String name;

    public EventInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "EventInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
