package ru.fazziclay.schoolguide.datafixer.old.v36;

import java.util.UUID;

public class V36Event extends V36WeekTimeSegment {
    public UUID eventInfo;

    public V36Event(UUID eventInfo, int start, int end) {
        super(start, end);
        this.eventInfo = eventInfo;
    }
}
