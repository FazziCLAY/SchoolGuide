package ru.fazziclay.schoolguide;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.fazziclay.fazziclaylibs.NumberUtils;

public class Clock {
    public static Calendar getCurrentCalendar() {
        return new GregorianCalendar();
    }

    public static long getCurrentTimeInMillis() {
        return getCurrentCalendar().getTimeInMillis();
    }

    public static long getCurrentDayInMillis() {
        Calendar currentDay = new GregorianCalendar(getCurrentCalendar().get(Calendar.YEAR),
                getCurrentCalendar().get(Calendar.MONTH),
                getCurrentCalendar().get(Calendar.DAY_OF_MONTH));
        return currentDay.getTimeInMillis();
    }

    public static long secondsToDaySeconds(int lessonStartTime) {
        return getCurrentDayInMillis() + lessonStartTime;
    }

    public static String millisToString(long m) {
        return millisToString(m, false);
    }
    public static String millisToString(long m, boolean withoutSeconds) {
        String leftTime = "";
        int left = (int) (m/1000);

        int hours = (left / 3600);
        if (hours > 0) leftTime+= NumberUtils.intToFixedLengthString(hours, 2) + ":";
        leftTime+= NumberUtils.intToFixedLengthString((left % 3600) / 60, 2);

        if (!withoutSeconds) {
            leftTime+= ":" + NumberUtils.intToFixedLengthString(left % 60, 2);
        }



        return leftTime;
    }
}
