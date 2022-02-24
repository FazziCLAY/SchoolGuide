package ru.fazziclay.schoolguide.app;

import android.util.Log;

public class MilkLog {
    private static final boolean ENABLED = true;
    private static final boolean APP_TRACE_LOG = true;
    private static final boolean ANDROID_LOG = true;
    private static final String ANDROID_LOG_TAG = "MilkLog";

    public static void g(String message, Exception e) {
        if (!ENABLED) return;

        if (APP_TRACE_LOG) {
            if (SchoolGuideApp.isInstanceAvailable()) {
                SchoolGuideApp app = SchoolGuideApp.get();
                if (app != null) {
                    if (e == null) {
                        app.getAppTrace().point(message);
                    } else {
                        app.getAppTrace().point(message, e);
                    }
                }
            }
        }

        if (ANDROID_LOG) {
            try {
                Log.e(ANDROID_LOG_TAG, message, e);
            } catch (Exception ignored) {}
        }
    }

    public static void g(String message) {
        g(message, null);
    }
}
