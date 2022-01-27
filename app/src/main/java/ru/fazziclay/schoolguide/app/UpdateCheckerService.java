package ru.fazziclay.schoolguide.app;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.net.UnknownHostException;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.UpdateCenterActivity;
import ru.fazziclay.schoolguide.app.manifest.GlobalBuiltinSchedule;
import ru.fazziclay.schoolguide.app.manifest.GlobalKeys;
import ru.fazziclay.schoolguide.app.manifest.GlobalManager;
import ru.fazziclay.schoolguide.app.manifest.GlobalVersionManifest;
import ru.fazziclay.schoolguide.util.DataUtil;

public class UpdateCheckerService extends Service {
    public static final int HANDLER_DELAY = 5 * 1000;
    public static final int CHECK_DELAY = 60 * 60 * 1000;
    public static final int CHECK_DELAY_NO_INTERNET = 60 * 1000;
    public static final String AUTO_UPDATE_CACHE_FILE = "manifest_auto_update_cache.json";

    private SchoolGuideApp app;
    private File autoUpdateCacheFile;
    private ManifestAutoUpdateCache autoUpdateCache;

    private NotificationManagerCompat notificationManagerCompat;

    private Handler handler;
    private Runnable runnable;

    // он 5 секунд тормозит вагон поезда, если на пути лежит человек
    // за 5 секунд не сьебётся, будет что будет
    public static void speedCrutch() {
        // Технологический костыль, т.к. сервис запускается только во время инициализации
        // приложения. если он(сервис) запустится слишком быстро, будет рекурсия и всё
        // нафиг сломаеться
        // поэтому если мы слишком быстрые, то сон в 5 секунд нафиг.
        long startCrutch = System.currentTimeMillis();
        while (!SchoolGuideApp.isInstanceAvailable()) {
            if (System.currentTimeMillis() - startCrutch > 1000*5) break;
        }
        // костыль окончен
    }

    @Override
    public void onCreate() {
        speedCrutch();

        app = SchoolGuideApp.get(this);
        autoUpdateCacheFile = new File(app.getCacheDir(), AUTO_UPDATE_CACHE_FILE);
        autoUpdateCache = DataUtil.load(autoUpdateCacheFile, ManifestAutoUpdateCache.class);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        handler = new Handler(getMainLooper());
        runnable = () -> {
            tick();
            handler.postDelayed(runnable, HANDLER_DELAY);
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void tick() {
        long pass = System.currentTimeMillis() - autoUpdateCache.getLatestManifestAutoUpdated();
        if (pass > CHECK_DELAY) {
            updateLatestUpdateTime(System.currentTimeMillis());

            GlobalManager.get(this, new GlobalManager.GlobalManagerInterface() {
                @Override
                public void failed(Exception exception) {
                    if (exception instanceof UnknownHostException) {
                        updateLatestUpdateTime((System.currentTimeMillis() - CHECK_DELAY) + CHECK_DELAY_NO_INTERNET);
                    }
                    app.setUpdateAvailable(false);
                }

                @Override
                public void success(GlobalKeys keys, GlobalVersionManifest versionManifest, GlobalBuiltinSchedule builtinSchedule) {
                    if (versionManifest.latestVersion == null) return;
                    if (versionManifest.latestVersion.getCode() > SharedConstrains.APPLICATION_VERSION_CODE) {
                        sendUpdateNotify();
                        app.setUpdateAvailable(true);
                        return;
                    }
                    app.setUpdateAvailable(false);
                }
            });
        }
    }

    /**
     * Обновить кеш и сохранить его
     * @see ManifestAutoUpdateCache
     * **/
    private void updateLatestUpdateTime(long time) {
        autoUpdateCache.setLatestManifestAutoUpdated(time);
        saveCache();
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void sendUpdateNotify() {
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
    }

    private void saveCache() {
        try {
            DataUtil.save(autoUpdateCacheFile, autoUpdateCache);
        } catch (Exception ignored) {}
    }
}