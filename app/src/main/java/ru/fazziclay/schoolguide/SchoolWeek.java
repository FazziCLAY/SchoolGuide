package ru.fazziclay.schoolguide;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class SchoolWeek {
    public static SchoolWeek getSchoolWeek() {
        return Config.getConfig().schoolWeek;
    }

    SchoolDay monday;
    SchoolDay tuesday;
    SchoolDay wednesday;
    SchoolDay thursday;
    SchoolDay friday;

    public SchoolWeek(SchoolDay monday, SchoolDay tuesday, SchoolDay wednesday, SchoolDay thursday, SchoolDay friday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
    }

    public LinkedList<SchoolDay> getList() {
        LinkedList<SchoolDay> schoolDays = new LinkedList<>();
        schoolDays.add(monday);
        schoolDays.add(tuesday);
        schoolDays.add(wednesday);
        schoolDays.add(thursday);
        schoolDays.add(friday);

        return schoolDays;
    }

    public SchoolDay getCurrentDay() {
        Calendar calendar = new GregorianCalendar();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.MONDAY) return monday;
        if (dayOfWeek == Calendar.TUESDAY) return tuesday;
        if (dayOfWeek == Calendar.WEDNESDAY) return wednesday;
        if (dayOfWeek == Calendar.THURSDAY) return thursday;
        if (dayOfWeek == Calendar.FRIDAY) return friday;

        return null;
    }
}
