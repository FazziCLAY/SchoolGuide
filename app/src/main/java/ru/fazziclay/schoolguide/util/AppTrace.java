package ru.fazziclay.schoolguide.util;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.fazziclay.schoolguide.SharedConstrains;

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
                    "\n" +
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
                    "$(message)" +
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
    private Map<Thread, StackTraceElement[]> initAllThreadStackTraces;

    private final List<Point> points = new ArrayList<>();

    public AppTrace(String initMessage) {
        ignoreException(() -> this.initMessage = initMessage);
        ignoreException(() -> this.initTimeMillis = System.currentTimeMillis());
        ignoreException(() -> this.initThread = Thread.currentThread());
        ignoreException(() -> this.initStackTrace = new Exception().getStackTrace());
        ignoreException(() -> this.initActiveThreadsCount = Thread.activeCount());
        ignoreException(() -> this.initAllThreadStackTraces = Thread.getAllStackTraces());
    }

    public AppTrace() {
        this(null);
    }

    private void ignoreException(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            point("AppTrace init error", throwable);
        }
    }

    public void point(String message, Throwable throwable) {
        long millis = System.currentTimeMillis();
        long nanos = System.nanoTime();
        points.add(new Point(
                Thread.currentThread(),
                new Exception().getStackTrace(),
                message,
                throwable,
                millis,
                nanos));
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
                    {"thread", thread.getName()},
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
            temp.append("\n");
            for (String s : split) {
                temp.append("\\ ").append(s).append("\n");
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
            temp.append("\tat ").append(traceElement.toString());
            if (i < maxI) temp.append("\n");
            i++;
        }

        return temp.toString();
    }

    private static String variable(String original, String key, Object value) {
        return original.replace("$(" + key + ")", value == null ? "null" : value.toString());
    }

    private static String variable(String original, Object[][] vars) {
        for (Object[] var : vars) {
            original = variable(original, (String) var[0], var[1]);
        }
        return original;
    }

    public static void saveAndLog(Context context, AppTrace t) {
        try {
            FileUtil.write(new File(context.getExternalCacheDir(), "latestAppTrace.txt"), t.getText());
        } catch (Exception ignored) {}
    }
}
