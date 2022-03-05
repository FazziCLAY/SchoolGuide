package ru.fazziclay.schoolguide.util;

public interface DebugPointer {
    void point(String tag, String message, Throwable throwable);

    void point(String tag, String message);

    void point(String message, Throwable throwable);

    void point(String message);
}
