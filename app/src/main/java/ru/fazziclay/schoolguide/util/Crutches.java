package ru.fazziclay.schoolguide.util;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class Crutches {
    /**
     * Уходит в цикл если инстанс приложения не доступен, к цикле максимум крутится 5 секунд, потом выходит из цикла.
     * **/
    public static void appInitializationDelay() {
        long startCrutch = System.currentTimeMillis();
        while (!SchoolGuideApp.isInstanceAvailable()) {
            if (System.currentTimeMillis() - startCrutch > 1000*5) break;
        }
    }

    /*private void sendUpdateNotify() {
        final int NOTIFICATION_ID = UpdateCenterActivity.NOTIFICATION_ID;
        final String NOTIFICATION_CHANNEL_ID = UpdateCenterActivity.NOTIFICATION_CHANNEL_ID;

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, UpdateCenterActivity.getLaunchIntent(this), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, UpdateCenterActivity.getLaunchIntent(this), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(null)
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setContentTitle(getString(R.string.updatecenter_notification_title))
                .setContentText(getString(R.string.updatecenter_notification_text))
                .setContentIntent(pendingIntent)
                .build();

        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }*/
}
