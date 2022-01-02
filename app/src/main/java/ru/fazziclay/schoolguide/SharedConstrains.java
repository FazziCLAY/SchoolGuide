package ru.fazziclay.schoolguide;

public class SharedConstrains {
    public static final int APPLICATION_VERSION_CODE    = BuildConfig.VERSION_CODE;
    public static final String APPLICATION_VERSION_NAME = BuildConfig.VERSION_NAME;

    public static final String SCHEDULE_NOTIFICATION_CHANNEL_ID = "main";
    public static final short SCHEDULE_NOTIFICATION_ID = 1;

    public static final String JSON_EMPTY_OBJECT = "{}";
    public static final String JSON_EMPTY_ARRAY = "[]";

    public static final short LOOP_DELAY = 1000;

    public static final int SECONDS_IN_MINUTE = 60;
    public static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;
    public static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;
    public static final int SECONDS_IN_WEEK = SECONDS_IN_DAY * 7;
}
