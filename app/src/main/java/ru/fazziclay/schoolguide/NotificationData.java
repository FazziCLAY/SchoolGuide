package ru.fazziclay.schoolguide;

import android.app.PendingIntent;

public class NotificationData {
    String title;
    String content;
    String sub;

    boolean isProgress;
    boolean isProgressIndeterminate;
    int progressMax;
    int progress;

    PendingIntent pendingIntent;

    public NotificationData() {}
}
