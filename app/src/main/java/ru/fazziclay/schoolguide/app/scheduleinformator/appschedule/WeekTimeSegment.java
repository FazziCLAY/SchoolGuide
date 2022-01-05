package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class WeekTimeSegment {
    int start;
    int end;

    public WeekTimeSegment(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean isNow() {
        int x = TimeUtil.getWeekSeconds();
        return ((start <= x) && (x <= end));
    }

    // Осталось до начала
    public int remainsUntilStart() {
        return start + 1 - TimeUtil.getWeekSeconds();
    }

    public int remainsUntilEnd() {
        return end + 1 - TimeUtil.getWeekSeconds();
    }

    public boolean isPassed() {
        return TimeUtil.getWeekSeconds() > end;
    }

    public boolean isNextWeek() { // Кто понимает как сделать так что бы не было линии разреза между концом и началом недели? Поможете? :) fazziclay.github.io
        return end > (24 * 60 * 60) * 7;
    }
}
