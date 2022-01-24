package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class Preset implements Cloneable {
    String name;
    String author;

    public HashMap<UUID, EventInfo> eventsInfos = new HashMap<>();
    public List<Event> eventsPositions = new ArrayList<>();

    public Preset() {}

    public Preset(String name) {
        this.name = name;
    }

    public Event getNowEvent() {
        int i = 0;
        while (i < eventsPositions.size()) {
            Event event = eventsPositions.get(i);
            if (event.isNow()) return event;
            i++;
        }
        return null;
    }

    public Event getNextEvent() {
        Event event = null;
        int m = Integer.MAX_VALUE;

        int i = 0;
        while (i < eventsPositions.size()) {
            Event event1 = eventsPositions.get(i);
            if (TimeUtil.getWeekSeconds() < event1.start && event1.start < m) {
                event = event1;
                m = event1.start;
            }
            i++;
        }
        return event;
    }

    public CompressedEvent getNowCompressedEvent() {
        return compressEvent(getNowEvent());
    }

    public CompressedEvent getNextCompressedEvent() {
        return compressEvent(getNextEvent());
    }

    public CompressedEvent compressEvent(Event event) {
        if (event == null) return null;
        return CompressedEvent.create(event, getEventInfo(event.eventInfo));
    }

    public EventInfo getEventInfo(UUID uuid) {
        return eventsInfos.get(uuid);
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    @NonNull
    public Preset clone() throws CloneNotSupportedException {
        return (Preset) super.clone();
    }
}
