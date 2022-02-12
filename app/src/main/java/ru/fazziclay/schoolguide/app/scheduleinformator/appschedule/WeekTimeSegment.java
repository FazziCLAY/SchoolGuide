package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class WeekTimeSegment {
    private static final int SECONDS_IN_DAY = 24 * 60 * 60;
    private static final int SECONDS_IN_WEEK = SECONDS_IN_DAY * 7;

    private int start;
    private int end;

    public WeekTimeSegment(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean isNow() {
        int x = TimeUtil.getWeekSeconds();
        if (isNextWeek()) {
            return !((start <= x) && (x <= end));
        } else {
            return ((start <= x) && (x <= end));
        }
    }

    // Осталось до начала
    public int remainsUntilStart() {
        return start - TimeUtil.getWeekSeconds();
    }

    public int remainsUntilEnd() {
        return (isNextWeek() ? toNextWeek(end) : end) - TimeUtil.getWeekSeconds();
    }

    public boolean isNextWeek() { // Кто понимает как сделать так что бы не было линии разреза между концом и началом недели? Поможете? :) fazziclay.github.io
        return start > end;
    }

    public int toNextWeek(int i) {
        return i + SECONDS_IN_WEEK;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
