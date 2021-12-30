package ru.fazziclay.schoolguide;

import ru.fazziclay.schoolguide.data.manifest.AppVersion;

/**
 * @author FazziCLAY
 * Все основные константы для приложения, некоторые автоматически берутся ещё от куда то но всё равно приложение обращается к этим а не тем
 * */
public class SharedConstrains {
    // = = V E R S I O N   S E T T I N G S
    public static final int APPLICATION_VERSION_CODE    = BuildConfig.VERSION_CODE;
    public static final String APPLICATION_VERSION_NAME = BuildConfig.VERSION_NAME;
    public static final AppVersion APPLICATION_VERSION  = new AppVersion(APPLICATION_VERSION_CODE, APPLICATION_VERSION_NAME);
    // = = =============================== = =

    public static final String MAIN_NOTIFICATION_CHANNEL_ID = "main";
    public static final short MAIN_NOTIFICATION_ID = 1;

    public static final String UPDATE_AVAILABLE_NOTIFICATION_CHANNEL_ID = "update_available";
    public static final short UPDATE_AVAILABLE_NOTIFICATION_ID = 2;

    public static final String CRASH_NOTIFICATION_CHANNEL_ID = "crash";
    public static final short CRASH_NOTIFICATION_ID = 555;

    public static final long[] VIBRATION_NOTIFY_LESSON = {0, 300, 200, 600, 200, 300};
    public static final long[] VIBRATION_NOTIFY_REST = {0, 250, 200, 250, 200, 250, 200};
    public static final long[] VIBRATION_NOTIFY_REST_ENDING = {0, 250, 250, 220, 100, 220, 100, 220};
    public static final long[] VIBRATION_NOTIFY_END = {0, 100, 400, 100, 400, 100, 400, 400, 400, 100, 100};

    public static final int UPDATE_MANIFEST_DELAY = 4 * 60 * 60;
    public static final int UPDATE_MANIFEST_DELAY_ACTIVITY = 2 * 60;
}
