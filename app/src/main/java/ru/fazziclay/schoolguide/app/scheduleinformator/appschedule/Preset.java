package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class Preset implements Cloneable {
    private String name;
    private String author;

    private boolean oneDayMode = false;
    private boolean syncedByGlobal = false;

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
            if (TimeUtil.getWeekSeconds() < event1.getStart() && event1.getEnd() < m) {
                event = event1;
                m = event1.getStart();
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
        return CompressedEvent.create(event, getEventInfo(event.getEventInfo()));
    }

    @NonNull
    public Preset clone() {
        try {
            return (Preset) super.clone();
        } catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
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

    public boolean isSyncedByGlobal() {
        return syncedByGlobal;
    }

    public void setSyncedByGlobal(boolean syncedByGlobal) {
        this.syncedByGlobal = syncedByGlobal;
    }

    public boolean isOneDayMode() {
        return oneDayMode;
    }

    public void setOneDayMode(boolean oneDayMode) {
        this.oneDayMode = oneDayMode;
    }
}
