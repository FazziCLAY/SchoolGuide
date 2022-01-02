package ru.fazziclay.schoolguide;

import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;

public class NotificationData {
    int icon;
    String title;
    String content;
    String sub;
    NotificationCompat.InboxStyle inboxStyle;

    boolean isProgress;
    boolean isProgressIndeterminate;
    int progressMax;
    int progress;

    PendingIntent pendingIntent;
}
