package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import java.util.UUID;

public class CompressedEvent extends WeekTimeSegment {
    String name;
    UUID eventInfoUUID;

    public CompressedEvent(String name, UUID eventInfoUUID, int start, int end) {
        super(start, end);
        this.name = name;
        this.eventInfoUUID = eventInfoUUID;
    }

    public static CompressedEvent create(Event event, EventInfo eventInfo) {
        return new CompressedEvent(eventInfo.name, event.eventInfo, event.start, event.end);
    }

    public String getName() {
        return name;
    }

    public UUID getEventInfoUUID() {
        return eventInfoUUID;
    }
}
