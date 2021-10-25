package ru.fazziclay.schoolguide;

public class SharedConstrains {
    public static final String FOREGROUND_NOTIFICATION_CHANNEL_ID = "Foreground";
    public static final short FOREGROUND_NOTIFICATION_ID = 1;

    public static final String EXTERNAL_NOTIFICATION_CHANNEL_ID = "External";
    public static final short EXTERNAL_NOTIFICATION_ID = 2;


    public static final long[] VIBRATION_NOTIFY_LESSON = {0, 300, 200, 600, 200, 300};
    public static final long[] VIBRATION_NOTIFY_REST = {0, 250, 200, 250, 200, 250, 200};
    public static final long[] VIBRATION_NOTIFY_REST_ENDING = {0, 250, 250, 220, 100, 220, 100, 220};
    public static final long[] VIBRATION_NOTIFY_END = {0, 100, 400, 100, 400, 100, 400, 400, 400, 100, 100};

}
