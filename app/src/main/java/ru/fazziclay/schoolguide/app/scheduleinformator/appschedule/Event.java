package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import java.util.UUID;

public class Event extends WeekTimeSegment {
    private UUID eventInfo;

    public Event(UUID eventInfo, int start, int end) {
        super(start, end);
        this.eventInfo = eventInfo;
    }

    public UUID getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(UUID eventInfo) {
        this.eventInfo = eventInfo;
    }
}
