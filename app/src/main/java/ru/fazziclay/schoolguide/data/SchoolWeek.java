package ru.fazziclay.schoolguide.data;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import ru.fazziclay.schoolguide.android.service.ForegroundService;

public class SchoolWeek {
    public static SchoolWeek getSchoolWeek() {
        return ForegroundService.getInstance().getSchoolWeek();
    }

    SchoolDay monday;
    SchoolDay tuesday;
    SchoolDay wednesday;
    SchoolDay thursday;
    SchoolDay friday;
    SchoolDay saturday;
    SchoolDay sunday;

    public SchoolWeek(SchoolDay monday,
                      SchoolDay tuesday,
                      SchoolDay wednesday,
                      SchoolDay thursday,
                      SchoolDay friday,
                      SchoolDay saturday,
                      SchoolDay sunday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public LinkedList<SchoolDay> getList() {
        LinkedList<SchoolDay> schoolDays = new LinkedList<>();
        schoolDays.add(monday);
        schoolDays.add(tuesday);
        schoolDays.add(wednesday);
        schoolDays.add(thursday);
        schoolDays.add(friday);
        schoolDays.add(saturday);
        schoolDays.add(sunday);

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
