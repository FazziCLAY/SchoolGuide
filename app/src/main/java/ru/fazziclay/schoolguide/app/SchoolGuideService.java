package ru.fazziclay.schoolguide.app;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

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

public class SchoolGuideService extends Service {
    private SchoolGuideApp app;

    private File autoUpdateCacheFile;
    private ManifestAutoUpdateCache autoUpdateCache;

    private Handler manifestUpdaterHandler;
    private Runnable manifestUpdaterRunnable;

    @Override
    public void onCreate() {
        app = SchoolGuideApp.get(this);

        autoUpdateCacheFile = new File(app.getCacheDir(), "manifest_auto_update_cache.json");
        autoUpdateCache = DataUtil.load(autoUpdateCacheFile, ManifestAutoUpdateCache.class);

        final int CHECK_DELAY = 60*60*1000;
        manifestUpdaterHandler = new Handler(Looper.myLooper());
        manifestUpdaterRunnable = () -> {
            long left = System.currentTimeMillis() - autoUpdateCache.latestManifestAutoUpdated;
            if (left > CHECK_DELAY) {
                autoUpdateCache.latestManifestAutoUpdated = System.currentTimeMillis();
                saveCache();

                GlobalManager.get(this, new GlobalManager.GlobalManagerInterface() {
                    @Override
                    public void failed(Exception exception) {
                        if (exception instanceof UnknownHostException) {
                            autoUpdateCache.latestManifestAutoUpdated = (System.currentTimeMillis() - CHECK_DELAY) + 60*1000;
                            saveCache();
                        }
                    }

                    @Override
                    public void success(GlobalKeys keys, GlobalVersionManifest versionManifest, GlobalBuiltinSchedule builtinSchedule) {
                        if (versionManifest.latestVersion == null) {
                            return;
                        }

                        if (versionManifest.latestVersion.getCode() > SharedConstrains.APPLICATION_VERSION_CODE) {
                            sendUpdateNotify();
                        }
                    }
                });
            }

            manifestUpdaterHandler.postDelayed(manifestUpdaterRunnable, 5000);
        };
    }

    private void sendUpdateNotify() {
        int id = UpdateCenterActivity.NOTIFICATION_ID;
        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag = flag | PendingIntent.FLAG_IMMUTABLE;
        }
        @SuppressLint("UnspecifiedImmutableFlag")
        Notification notification = new NotificationCompat.Builder(this, UpdateCenterActivity.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(null)
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setContentTitle(getString(R.string.updatecenter_notification_title))
                .setContentText(getString(R.string.updatecenter_notification_text))
                .setContentIntent(PendingIntent.getActivity(this, 0, UpdateCenterActivity.getLaunchIntent(this), flag))
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(id, notification);
    }

    private void saveCache() {
        try {
            DataUtil.save(autoUpdateCacheFile, autoUpdateCache);
        } catch (Exception ignored) {}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manifestUpdaterHandler.post(manifestUpdaterRunnable);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}