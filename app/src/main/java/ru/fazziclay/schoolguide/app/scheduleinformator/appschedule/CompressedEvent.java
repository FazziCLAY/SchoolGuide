package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

public class CompressedEvent extends WeekTimeSegment {
    String name;

    public CompressedEvent(String name, int start, int end) {
        super(start, end);
        this.name = name;
    }

    public static CompressedEvent create(Event event, EventInfo eventInfo) {
        return new CompressedEvent(eventInfo.name, event.start, event.end);
    }

    public String getName() {
        return name;
    }
}
