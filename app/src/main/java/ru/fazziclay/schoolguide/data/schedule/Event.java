package ru.fazziclay.schoolguide.data.schedule;

import java.util.UUID;

import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class Event extends TimeSegment {
    UUID eventUUID;

    public Event(UUID eventUUID, int startTime, int endTime) {
        super(startTime, endTime);
        this.eventUUID = eventUUID;
    }

    public UUID getEventUUID() {
        return eventUUID;
    }

    public void setEventUUID(UUID eventUUID) {
        this.eventUUID = eventUUID;
    }

    public boolean isCurrent() {
        int x = TimeUtil.getWeekSeconds();
        return (getStartTime() <= x) && (x <= getEndTime());
    }
}
