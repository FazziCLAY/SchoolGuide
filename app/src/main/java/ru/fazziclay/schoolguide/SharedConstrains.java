package ru.fazziclay.schoolguide;

import ru.fazziclay.schoolguide.data.manifest.AppVersion;

public class SharedConstrains {
    // = = V E R S I O N   S E T T I N G S
    public static final int APPLICATION_VERSION_CODE = BuildConfig.VERSION_CODE;
    public static final String APPLICATION_VERSION_NAME = BuildConfig.VERSION_NAME;
    //public static final boolean APPLICATION_VERSION_IS_UNSTABLE = true;
    public static final AppVersion APP_VERSION = new AppVersion(APPLICATION_VERSION_CODE, APPLICATION_VERSION_NAME);
    // = = =============================== = =

    public static final boolean DEV_FEATURED_MANIFEST_ONLY_FILE = false;
    public static final int DEV_FEATURED_MANIFEST_GLOBAL_DELAY = 3000;

    public static final String FOREGROUND_NOTIFICATION_CHANNEL_ID = "Foreground";
    public static final short FOREGROUND_NOTIFICATION_ID = 1;

    public static final String EXTERNAL_NOTIFICATION_CHANNEL_ID = "External";
    public static final short EXTERNAL_NOTIFICATION_ID = 2;

    public static final String UPDATECHECKER_NOTIFICATION_CHANNEL_ID = "UpdateChecker";
    public static final short UPDATECHECKER_NOTIFICATION_ID = 3;


    public static final long[] VIBRATION_NOTIFY_LESSON = {0, 300, 200, 600, 200, 300};
    public static final long[] VIBRATION_NOTIFY_REST = {0, 250, 200, 250, 200, 250, 200};
    public static final long[] VIBRATION_NOTIFY_REST_ENDING = {0, 250, 250, 220, 100, 220, 100, 220};
    public static final long[] VIBRATION_NOTIFY_END = {0, 100, 400, 100, 400, 100, 400, 400, 400, 100, 100};

    public static final int INTERNET_TERMINATOR_DELAY = 10; //10 * 60;
}
