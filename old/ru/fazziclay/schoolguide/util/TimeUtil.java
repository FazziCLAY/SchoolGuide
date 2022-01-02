package ru.fazziclay.schoolguide.util;

public class TimeUtil {
    public static int getHoursInSeconds(int seconds) {
        return seconds / 3600;
    }

    public static int getMinutesInSeconds(int seconds) {
        return (seconds % 3600) / 60;
    }

    public static int getSecondsInSeconds(int seconds) {
        return seconds % 60;
    }

    public static String  secondsToHumanTime(int seconds, boolean forceHour) {
        return ( ((forceHour || getHoursInSeconds(seconds) > 0) ? toFixed(getHoursInSeconds(seconds), 2)+":" : "") + toFixed(getMinutesInSeconds(seconds), 2) + ":" + toFixed(getSecondsInSeconds(seconds), 2) );
    }

    public static String toFixed(int number, int fixedLength) {
        return String.format("%0"+fixedLength+"d", number);
    }
}
