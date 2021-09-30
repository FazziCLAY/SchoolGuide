package ru.fazziclay.fazziclaylibs;

public class TimeUtil {
    public static String secondsToDigitalTime(long m) {
        return secondsToDigitalTime(m, false);
    }

    public static String secondsToDigitalTime(long m, boolean withoutSeconds) {
        return secondsToDigitalTime(m, withoutSeconds, false);
    }

    public static String secondsToDigitalTime(long m, boolean withoutSeconds, boolean prinudilHours) {
        String leftTime = "";
        int left = (int) (m);

        int hours = (left / 3600);
        if (hours > 0 || prinudilHours) leftTime+= NumberUtils.intToFixedLengthString(hours, 2) + ":";
        leftTime+= NumberUtils.intToFixedLengthString((left % 3600) / 60, 2);

        if (!withoutSeconds) {
            leftTime+= ":" + NumberUtils.intToFixedLengthString(left % 60, 2);
        }

        return leftTime;
    }
}
