package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ScheduleWeek implements Cloneable {
    public List<ScheduledLesson> monday    = new ArrayList<>();
    public List<ScheduledLesson> tuesday   = new ArrayList<>();
    public List<ScheduledLesson> wednesday = new ArrayList<>();
    public List<ScheduledLesson> thursday  = new ArrayList<>();
    public List<ScheduledLesson> friday    = new ArrayList<>();
    public List<ScheduledLesson> saturday  = new ArrayList<>();
    public List<ScheduledLesson> sunday    = new ArrayList<>();

    public ScheduleWeek(List<ScheduledLesson> monday,
                        List<ScheduledLesson> tuesday,
                        List<ScheduledLesson> wednesday,
                        List<ScheduledLesson> thursday,
                        List<ScheduledLesson> friday,
                        List<ScheduledLesson> saturday,
                        List<ScheduledLesson> sunday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public ScheduleWeek() {}

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

    @NonNull
    @Override
    public String toString() {
        return "ScheduleWeek{" +
                "monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", sunday=" + sunday +
                '}';
    }

    @NonNull
    public ScheduleWeek clone() {
        List<ScheduledLesson> _monday    = new ArrayList<>();
        List<ScheduledLesson> _tuesday   = new ArrayList<>();
        List<ScheduledLesson> _wednesday = new ArrayList<>();
        List<ScheduledLesson> _thursday  = new ArrayList<>();
        List<ScheduledLesson> _friday    = new ArrayList<>();
        List<ScheduledLesson> _saturday  = new ArrayList<>();
        List<ScheduledLesson> _sunday    = new ArrayList<>();

        int i = 0;

        while (i < monday.size()) {
            _monday.add(monday.get(i).clone());
            i++;
        }

        i = 0;
        while (i < tuesday.size()) {
            _tuesday.add(tuesday.get(i).clone());
            i++;
        }

        i = 0;
        while (i < wednesday.size()) {
            _wednesday.add(wednesday.get(i).clone());
            i++;
        }

        i = 0;
        while (i < thursday.size()) {
            _thursday.add(thursday.get(i).clone());
            i++;
        }

        i = 0;
        while (i < friday.size()) {
            _friday.add(friday.get(i).clone());
            i++;
        }

        i = 0;
        while (i < saturday.size()) {
            _saturday.add(saturday.get(i).clone());
            i++;
        }

        i = 0;
        while (i < sunday.size()) {
            _sunday.add(sunday.get(i).clone());
            i++;
        }

        return new ScheduleWeek(_monday, _tuesday, _wednesday, _thursday, _friday, _saturday, _sunday);
    }
}
