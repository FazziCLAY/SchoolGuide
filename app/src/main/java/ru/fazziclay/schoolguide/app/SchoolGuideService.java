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

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.UpdateCenterActivity;
import ru.fazziclay.schoolguide.util.DataUtil;
import ru.fazziclay.schoolguide.util.NetworkUtil;

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

        manifestUpdaterHandler = new Handler(Looper.myLooper());
        manifestUpdaterRunnable = () -> {
            long left = System.currentTimeMillis() - autoUpdateCache.latestManifestAutoUpdated;
            if (left > 60 * 60 * 1000) {
                Thread thread = new Thread(() -> {
                    try {
                        String remoteManifest = NetworkUtil.parseTextPage(SharedConstrains.VERSION_MANIFEST_V2);
                        Manifest manifest = app.getGson().fromJson(remoteManifest, Manifest.class);
                        if (manifest == null || manifest.latest == null || manifest.latest.release == null || manifest.latest.release.getCode() <= SharedConstrains.APPLICATION_VERSION_CODE) {
                            return;
                        }
                        runOnUiThread(() -> {
                            try {
                                int id = UpdateCenterActivity.NOTIFICATION_ID;
                                int flag = PendingIntent.FLAG_UPDATE_CURRENT;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    flag = flag | PendingIntent.FLAG_IMMUTABLE;
                                }
                                @SuppressLint("UnspecifiedImmutableFlag") Notification notification = new NotificationCompat.Builder(SchoolGuideService.this, UpdateCenterActivity.NOTIFICATION_CHANNEL_ID)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setSound(null)
                                        .setOnlyAlertOnce(true)
                                        .setSilent(true)
                                        .setContentTitle(getString(R.string.updatecenter_notification_title))
                                        .setContentText(getString(R.string.updatecenter_notification_text))
                                        .setContentIntent(PendingIntent.getActivity(SchoolGuideService.this, 0, UpdateCenterActivity.getLaunchIntent(SchoolGuideService.this), flag))
                                        .build();
                                app.getScheduleInformatorApp().sendNotify(id, notification);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (UnknownHostException ignored) {
                        autoUpdateCache.latestManifestAutoUpdated = System.currentTimeMillis() - ((60*60*1000) - (3*1000));
                        try {
                            saveCache();
                        } catch (Exception ignored1) {}
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                autoUpdateCache.latestManifestAutoUpdated = System.currentTimeMillis();
                try {
                    saveCache();
                } catch (Exception ignored) {}
                thread.start();
            }

            manifestUpdaterHandler.postDelayed(manifestUpdaterRunnable, 5000);
        };
    }

    private void saveCache() {
        DataUtil.save(autoUpdateCacheFile, autoUpdateCache);
    }

    public void runOnUiThread(Runnable r) {
        r.run(); // TODO: 2022-01-23 fix
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