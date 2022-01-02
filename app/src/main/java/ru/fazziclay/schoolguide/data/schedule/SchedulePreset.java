package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchedulePreset {
    String name = "Unknown Preset " + UUID.randomUUID().toString();
    String author;

    List<Event> events = new ArrayList<>();

    @NonNull
    @Override
    public String toString() {
        return "SchedulePreset{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", events=" + events +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Event> getEvents() {
        return events;
    }
}
