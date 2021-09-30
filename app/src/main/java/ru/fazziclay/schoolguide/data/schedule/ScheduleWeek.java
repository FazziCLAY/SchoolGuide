package ru.fazziclay.schoolguide.data.schedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleWeek {
    public List<ScheduledLesson> monday    = new ArrayList<>();
    public List<ScheduledLesson> tuesday   = new ArrayList<>();
    public List<ScheduledLesson> wednesday = new ArrayList<>();
    public List<ScheduledLesson> thursday  = new ArrayList<>();
    public List<ScheduledLesson> friday    = new ArrayList<>();
    public List<ScheduledLesson> saturday  = new ArrayList<>();
    public List<ScheduledLesson> sunday    = new ArrayList<>();

    public List<List<ScheduledLesson>> getInList() {
        List<List<ScheduledLesson>> list = new ArrayList<>();
        list.add(monday);
        list.add(tuesday);
        list.add(wednesday);
        list.add(thursday);
        list.add(friday);
        list.add(saturday);
        list.add(sunday);

        return list;
    }
}
