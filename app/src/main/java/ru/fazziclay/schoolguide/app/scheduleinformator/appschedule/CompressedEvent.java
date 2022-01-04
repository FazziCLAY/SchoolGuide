package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

public class CompressedEvent extends WeekTimeSegment {
    public String name;

    public CompressedEvent(String name, int start, int end) {
        super(start, end);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
