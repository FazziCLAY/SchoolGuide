package ru.fazziclay.schoolguide.util;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class AppTrace {
    private final long initTime;
    private final Thread initializeInThread;
    private final int activeThreadsCount;
    private final StackTraceElement[] stackTraceToInitializer;
    private final Map<Thread, StackTraceElement[]> allStackTrace;

    private final List<Trace> traces = new ArrayList<>();

    private Throwable throwable = null;

    public AppTrace() {
        initTime = System.currentTimeMillis();
        initializeInThread = Thread.currentThread();
        activeThreadsCount = Thread.activeCount();
        stackTraceToInitializer = new Exception().getStackTrace();
        allStackTrace = Thread.getAllStackTraces();
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void trace(String message) {
        long millis = System.currentTimeMillis();
        long nanos = System.nanoTime();
        Trace trace = new Trace(Thread.currentThread(), new Exception().getStackTrace(), message, millis, nanos);
        traces.add(trace);
    }

    public class Trace {
        private final Thread thread;
        private final StackTraceElement[] stack;
        private final String message;

        private final long timeMillis;
        private final long timeNanos;

        public Trace(Thread thread, StackTraceElement[] stack, String message, long timeMillis, long timeNanos) {
            this.thread = thread;
            this.stack = stack;
            this.message = message;
            this.timeMillis = timeMillis;
            this.timeNanos = timeNanos;
        }

        public String toStr() {
            return String.format("+ thread: %s\n+ time: (%s/%s)\n+ = STACKTRACE =\n%s\n+ - STACKTRACE -",
                    thread.getName(), timeMillis, timeNanos, stackTraceToStr(stack)
            );
        }

        public String getMessage() {
            return message;
        }
    }

    public String getText() {
        String s = "==== AppTrace ====" +
                "\nInit:" +
                "\n* Time: $(time)" +
                "\n* Thread: $(initializeInThread)" +
                "\nDetails:" +
                "\n* Application:" +
                "\n* * code: $(details/application/code)" +
                "\n* * name: $(details/application/name)" +
                "\n* * id: $(details/application/id)" +
                "\n* * buildType: $(details/application/buildType)" +
                "\n" +
                "\n* SchoolGuideApp:" +
                "\n* * toString: $(details/schoolguideapp/toString)" +
                "\n*" +
                "\n* Device:" +
                "\n* * ANDROID: $(details/device/android)" +
                "\n" +
                "\nThrowable: $(throwable/toString)" +
                "\n$(throwable/stacktrace)" +
                "\n" +
                "\nTraces:" +
                "\n$(traces)" +
                "\n" +
                "\nInitStackTrace:" +
                "\n$(initstacktrace)" +
                "\n" +
                "\nStackTraces(activeThreadsCount=$(stacktraces/activeThreadsCount)):" +
                "\n$(stacktraces)";

        String schoolGuideToString = (SchoolGuideApp.isInstanceAvailable() ? SchoolGuideApp.get().toString() : "(instance not available)");

        StringBuilder allStackTraceStr = new StringBuilder();
        int i = 0;
        for (Thread thread : allStackTrace.keySet()) {
            allStackTraceStr.append("[").append(i).append("] __ Thread: ").append(thread.getName()).append(" __");
            allStackTraceStr.append("\n").append(stackTraceToStr(allStackTrace.get(thread)));
            allStackTraceStr.append("\n[").append(i).append("] -- Thread: ").append(thread.getName()).append(" --\n\n");
            i++;
        }

        StringBuilder tracesStr = new StringBuilder();
        i = 0;
        for (Trace trace : traces) {
            tracesStr.append(String.format("[%s] === %s ===", i, trace.getMessage()));
            tracesStr.append("\n");
            tracesStr.append(trace.toStr());
            tracesStr.append("\n\n");
            i++;
        }

        s = var(s, "traces", tracesStr.toString());
        s = var(s, "time", initTime);
        s = var(s, "initializeInThread", initializeInThread.getName());
        s = var(s, "details/device/android", Build.VERSION.SDK_INT);
        s = var(s, "details/application/code", SharedConstrains.APPLICATION_VERSION_CODE);
        s = var(s, "details/application/name", SharedConstrains.APPLICATION_VERSION_NAME);
        s = var(s, "details/application/id", SharedConstrains.APPLICATION_ID);
        s = var(s, "details/application/buildType", SharedConstrains.APPLICATION_BUILD_TYPE);
        s = var(s, "details/schoolguideapp/toString", schoolGuideToString);
        s = var(s, "stacktraces/activeThreadsCount", activeThreadsCount);
        s = var(s, "initstacktrace", stackTraceToStr(stackTraceToInitializer));
        s = var(s, "throwable/toString", (throwable == null ? "null" : throwable.toString()));
        s = var(s, "throwable/stacktrace", (throwable == null ? "null" : stackTraceToStr(throwable.getStackTrace())));
        s = var(s, "stacktraces", allStackTraceStr.toString());

        return s;
    }

    public String stackTraceToStr(StackTraceElement[] trace) {
        if (trace == null) {
            return "(stacktrace is null)";
        }
        StringBuilder ret = new StringBuilder();

        int i = 0;
        for (StackTraceElement traceElement : trace) {
            ret.append("\tat ").append(traceElement);
            if (i != trace.length-1) ret.append("\n");
            i++;
        }

        return ret.toString();
    }

    private String var(String original, String key, Object value) {
        return original.replace("$("+key+")", value.toString());
    }

    public static AppTrace getInstance() {
        return new AppTrace();
    }

    public static void saveAndLog(Context context, AppTrace t) {
        try {
            FileUtil.write(new File(context.getExternalCacheDir(), "latestAppTrace.txt"), t.getText());
        } catch (Exception ignored) {}
    }
}
