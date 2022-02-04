package ru.fazziclay.schoolguide.util;

import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class AppTrace {
    private static final String TEXT_BASE =
            "0===== FazziCLAY AppTrace =====0\n" +
                    "--- Init ---\n" +
                    "timeMillis: $(init/timeMillis)\n" +
                    "thread: $(init/thread)\n" +
                    "message: $(init/message)\n" +
                    "\n" +
                    "Application: \n$(init/application)\n" +
                    "\n" +
                    "Device: \n$(init/device)\n" +
                    "\n" +
                    "--- Points ---\n" +
                    "$(points)\n" +
                    "\n" +
                    "--- Init StackTrace ---\n" +
                    "$(init/stacktrace)" +
                    "\n" +
                    "--- Init Threads ---\n" +
                    "// Active Count: $(threads/activeCount)\n" +
                    "$(threads)";

    private static final String TEXT_APPLICATION =
            "\\ code: $(code)\n" +
                    "\\ name: $(name)\n" +
                    "\\ id: $(id)\n" +
                    "\\ buildType: $(buildType)";

    private static final String TEXT_DEVICE =
            "\\ Android.SDK: $(android/sdk)\n" +
                "\\ Brand: $(brand)\n" +
                "\\ Manufacturer: (manufacturer)\n" +
                "\\ Bootloader: $(bootloader)";

    private static final String POINT_BASE =
            "+++ $(title) +++\n" +
                    "+ message: $(message)\n" +
                    "+ thread: $(thread)\n" +
                    "+ time: $(time)\n" +
                    "+ ___ STACKTRACE ___ +\n" +
                    "$(stacktrace)\n" +
                    "+ ___ THROWABLE ___ +\n" +
                    "$(throwable)";

    private long initTimeMillis;
    private Thread initThread;
    private String initMessage;
    private StackTraceElement[] initStackTrace;
    private int initActiveThreadsCount;
    //private Map<Thread, StackTraceElement[]> initAllThreadStackTraces;

    private final List<Point> points = new ArrayList<>();

    public AppTrace(String initMessage) {
        ignoreException(() -> this.initMessage = initMessage);
        ignoreException(() -> this.initTimeMillis = System.currentTimeMillis());
        ignoreException(() -> this.initThread = Thread.currentThread());
        ignoreException(() -> this.initStackTrace = new Exception().getStackTrace());
        ignoreException(() -> this.initActiveThreadsCount = Thread.activeCount());
        //ignoreException(() -> this.initAllThreadStackTraces = Thread.getAllStackTraces());
    }

    private void ignoreException(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            point("AppTrace init error", throwable);
        }
    }

    private boolean pointDebugLogException = false;
    public void point(String message, Throwable throwable) {
        long millis = System.currentTimeMillis();
        long nanos = System.nanoTime();
        Point point = new Point(
                Thread.currentThread(),
                new Exception().getStackTrace(),
                message,
                throwable,
                millis,
                nanos);
        points.add(point);
        try {
            Log.d("POINT", point.format(0));
        } catch (Exception e) {
            if (!pointDebugLogException) {
                pointDebugLogException = true;
                point("Print point message to Log.d exception!", e);
            }
        }
        try {
            if (SchoolGuideApp.isInstanceAvailable()) {
                SchoolGuideApp.get().saveAppTrace();
            }
        } catch (Exception ignored) {}
    }

    public void point(String message) {
        point(message, null);
    }

    private class Point {
        private final Thread thread;
        private final String message;
        private final Throwable throwable;
        private final StackTraceElement[] stackTrace;

        private final long timeMillis;
        private final long timeNanos;

        public Point(Thread thread, StackTraceElement[] stackTrace, String message, Throwable throwable, long timeMillis, long timeNanos) {
            this.thread = thread;
            this.stackTrace = stackTrace;
            this.message = message;
            this.throwable = throwable;
            this.timeMillis = timeMillis;
            this.timeNanos = timeNanos;
        }

        public String format(int position) {
            return variable(POINT_BASE, new Object[][]{
                    {"title", "Point #" + position},
                    {"message", formatMultilineMessage(message)},
                    {"thread", thread == null ? "null" : thread.getName()},
                    {"time", String.format("%s / %s", timeMillis, timeNanos)},
                    {"stacktrace", stackTraceToString(stackTrace)},
                    {"throwable", throwable == null ? null : String.format("Message: %s\nStackTrace:\n%s", throwable, stackTraceToString(throwable.getStackTrace()))}
            });
        }
    }

    private String formatMultilineMessage(String message) {
        if (message == null) return null;
        if (message.contains("\n")) {
            String[] split = message.split("\n");
            StringBuilder temp = new StringBuilder();
            for (String s : split) {
                temp.append("\n").append("\\ ").append(s);
            }
            return temp.toString();

        } else {
            return message;
        }
    }


    private String formatPoints() {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        while (i < points.size()) {
            Point point = points.get(i);
            if (point == null) continue;

            stringBuilder.append(point.format(i)).append("\n\n");
            i++;
        }

        return stringBuilder.toString();
    }

    public String getText() {
        return variable(TEXT_BASE, new Object[][]{
                {"init/timeMillis", initTimeMillis},
                {"init/thread", initThread.getName()},
                {"init/message", formatMultilineMessage(initMessage)},

                {"init/application", variable(TEXT_APPLICATION, new Object[][]{
                        {"code", SharedConstrains.APPLICATION_VERSION_CODE},
                        {"name", SharedConstrains.APPLICATION_VERSION_NAME},
                        {"id", SharedConstrains.APPLICATION_ID},
                        {"buildType", SharedConstrains.APPLICATION_BUILD_TYPE}
                })},

                {"init/device", variable(TEXT_DEVICE, new Object[][]{
                        {"android/sdk", Build.VERSION.SDK_INT},
                        {"brand", Build.BRAND},
                        {"manufacturer", Build.MANUFACTURER},
                        {"bootloader", Build.BOOTLOADER},
                })},
                {"points", formatPoints()},
                {"threads", "TODO add"},
                {"threads/activeCount", initActiveThreadsCount},
                {"init/stacktrace", stackTraceToString(initStackTrace)}
        });
    }

    /**
     * Превращает stackTrace в строку
     * <p>at ...</p>
     * <p>at ...</p>
     * <p>at ...</p>
     * **/
    private static String stackTraceToString(StackTraceElement[] trace) {
        if (trace == null) {
            return null;
        }
        StringBuilder temp = new StringBuilder();

        int i = 0;
        int maxI = trace.length;
        for (StackTraceElement traceElement : trace) {
            temp.append("\tat ").append(traceElement == null ? "null" : traceElement.toString());
            if (i < maxI) temp.append("\n");
            i++;
        }

        return temp.toString();
    }

    private static String variable(String original, String key, Object value) {
        if (key == null) return original;
        return original.replace("$(" + key + ")", value == null ? "null" : value.toString());
    }

    private static String variable(String original, Object[][] vars) {
        if (original == null) return "original null";
        for (Object[] var : vars) {
            try {
                original = variable(original, (String) var[0], var[1]);
            } catch (Exception e) {
                original = "=[!VARIABLE EXCEPTION!]=\n" + e + "\n\n" + original;
            }
        }
        return original;
    }
}
