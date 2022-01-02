package ru.fazziclay.schoolguide;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationManagerCompat;

import ru.fazziclay.schoolguide.android.service.TickService;
import ru.fazziclay.schoolguide.data.schedule.LocalSchedule;
import ru.fazziclay.schoolguide.data.schedule.Schedule;
import ru.fazziclay.schoolguide.data.settings.Settings;
import ru.fazziclay.schoolguide.datafixer.DataFixer;

/**
 * @author FazziCLAY
 * Главный класс приложения, в нём вся логика самого приложения, дабы не засорять этим кодом классы связанные с андроидом
 * */
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
        instance.load();
        return instance;
    }
    // ==================== STATIC ====================

    Context androidApplicationContext = null;
    TickService tickServiceInstance = null;
    Vibrator vibrator = null;

    DataFixer dataFixer = null;
    Settings settings = null;
    Schedule schedule = null;

    LocalSchedule selectedLocalSchedule = null;

    // Notification
    NotificationManagerCompat notificationManagerCompat = null;
    NotificationData notificationData = null;

    public SchoolGuide(Context context) {
        androidApplicationContext = context;
    }

    public void loadAndroidApp(Activity activity) {
        loadNotificationChannels();
        activity.startService(new Intent(activity, TickService.class));
        //activity.startActivity(new Intent(activity, HomeActivity.class));
    }

    // Загрузка
    private void load() {
        vibrator = (Vibrator) androidApplicationContext.getSystemService(Context.VIBRATOR_SERVICE);

        dataFixer = new DataFixer(androidApplicationContext);
        dataFixer.tryFix();

        settings = Settings.load(androidApplicationContext);
        schedule = Schedule.load(androidApplicationContext);

        // Notification
        notificationManagerCompat = NotificationManagerCompat.from(androidApplicationContext);
        notificationData = new NotificationData();
        notificationData.pendingIntent = settings.notificationStyle.clickAction.startActivityInterface.run(androidApplicationContext, this);

        updateSelectedLocalSchedule();
    }

    // Создать все необходимые каналы для уведомлений
    private void loadNotificationChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = androidApplicationContext.getSystemService(NotificationManager.class);
            NotificationChannel main = new NotificationChannel(SharedConstrains.MAIN_NOTIFICATION_CHANNEL_ID, getString(R.string.notificationChannel_main_name), NotificationManager.IMPORTANCE_NONE);
            NotificationChannel updateAvailable = new NotificationChannel(SharedConstrains.UPDATE_AVAILABLE_NOTIFICATION_CHANNEL_ID, getString(R.string.notificationChannel_updatechecker_name), NotificationManager.IMPORTANCE_HIGH);
            main.setDescription(getString(R.string.notificationChannel_main_description));
            updateAvailable.setDescription(getString(R.string.notificationChannel_updatechecker_description));
            notificationManager.createNotificationChannel(main);
            notificationManager.createNotificationChannel(updateAvailable);
        }
    }

    public void registerTickServiceInstance(TickService service) {
        this.tickServiceInstance = service;
    }

    public void tick() {

    }

    // Обновить переменную selectedLocalSchedule в соответствии с текущий выбором в настройках
    public void updateSelectedLocalSchedule() {
        //this.selectedLocalSchedule = schedule.getLocalSchedule(settings.selectedLocalSchedule);
        if (selectedLocalSchedule == null) selectedLocalSchedule = new LocalSchedule(getString(R.string.abc_unknown));
    }

    // get translated for R.string
    public String getString(int r) {
        return androidApplicationContext.getString(r);
    }

    // Провибрировать в нужный такт
    public void vibrate(long[] tact) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(tact, -1));
        } else {
            vibrator.vibrate(tact, -1);
        }
    }

    // Отправляет нужное notification с айди notificationId
    public void sendNotify(int notificationId, Notification notification) {
        notificationManagerCompat.notify(notificationId, notification);
    }

    // Отменяет нужное уведомление с айди notificationId
    public void cancelNotify(int notificationId) {
        notificationManagerCompat.cancel(notificationId);
    }
}
