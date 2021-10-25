package ru.fazziclay.schoolguide.data.schedule;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LocalSchedule {
    String name = "Unknown";

    List<Lesson> monday    = new ArrayList<>();
    List<Lesson> tuesday   = new ArrayList<>();
    List<Lesson> wednesday = new ArrayList<>();
    List<Lesson> thursday  = new ArrayList<>();
    List<Lesson> friday    = new ArrayList<>();
    List<Lesson> saturday  = new ArrayList<>();
    List<Lesson> sunday    = new ArrayList<>();

    public LocalSchedule(String name) {
        this.name = name;
    }

    public List<Lesson> get(int dayOfWeek) {
        if (dayOfWeek == Calendar.MONDAY)    return monday;
        if (dayOfWeek == Calendar.TUESDAY)   return tuesday;
        if (dayOfWeek == Calendar.WEDNESDAY) return wednesday;
        if (dayOfWeek == Calendar.THURSDAY)  return thursday;
        if (dayOfWeek == Calendar.FRIDAY)    return friday;
        if (dayOfWeek == Calendar.SATURDAY)  return saturday;
        if (dayOfWeek == Calendar.SUNDAY)    return sunday;
        throw new Error("dayOfWeek("+dayOfWeek+") is not found!");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "LocalSchedule{" +
                "name='" + name + '\'' +
                ", monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", sunday=" + sunday +
                '}';
    }
}
