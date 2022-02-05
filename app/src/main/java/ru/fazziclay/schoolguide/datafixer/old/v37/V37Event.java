package ru.fazziclay.schoolguide.datafixer.old.v37;

import java.util.UUID;

public class V37Event extends V37WeekTimeSegment {
    public UUID eventInfo;

    public V37Event(UUID eventInfo, int start, int end) {
        super(start, end);
        this.eventInfo = eventInfo;
    }
}
