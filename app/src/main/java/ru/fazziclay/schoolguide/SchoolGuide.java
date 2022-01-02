package ru.fazziclay.schoolguide;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import ru.fazziclay.schoolguide.android.TickService;
import ru.fazziclay.schoolguide.util.NotificationData;

public class SchoolGuide {
    // ==================== STATIC ====================
    private static SchoolGuide instance = null;

    // Получить приложение (без возможности контекста)
    public static SchoolGuide get() {
        return get(null);
    }

    // Получить приложение
    public static SchoolGuide get(Context context) {
        if (context != null && instance == null) {
            return create(context);
        }
        return instance;
    }

    // Созать приложение и вернуть
    private static SchoolGuide create(Context context) {
        instance = new SchoolGuide(context);
        return instance;
    }
    // ==================== STATIC ====================


    private Context androidContext;
    private TickService tickService;
    public NotificationData notificationData = new NotificationData();

    public SchoolGuide(Context context) {
        this.androidContext = context.getApplicationContext();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = androidContext.getSystemService(NotificationManager.class);
            NotificationChannel schedule = new NotificationChannel(SharedConstrains.SCHEDULE_NOTIFICATION_CHANNEL_ID, "Канал уведомления показывает расписание", NotificationManager.IMPORTANCE_NONE);
            schedule.setDescription("Описание канала");
            notificationManager.createNotificationChannel(schedule);
        }
    }

    public void registerTickServiceInstance(TickService tickService) {
        this.tickService = tickService;
    }

    public short tick() {


        return 1000;
    }

    public void sendNotify() {

    }
}
