package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import java.util.UUID;

public class Event extends WeekTimeSegment {
    UUID externalEvent;

    public Event(UUID externalEvent, int start, int end) {
        super(start, end);
        this.externalEvent = externalEvent;
    }
}
