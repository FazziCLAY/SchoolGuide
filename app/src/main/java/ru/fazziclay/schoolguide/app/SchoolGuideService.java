package ru.fazziclay.schoolguide.app;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.util.NetworkUtil;

public class SchoolGuideService extends Service {
    SchoolGuideApp app;

    ManifestAutoUpdateCache autoUpdateCache = new ManifestAutoUpdateCache();

    Handler manifestUpdaterHandler;
    Runnable manifestUpdaterRunnable;

    @Override
    public void onCreate() {
        app = SchoolGuideApp.get(this);

        manifestUpdaterHandler = new Handler(Looper.myLooper());
        manifestUpdaterRunnable = () -> {
            long left = System.currentTimeMillis() - autoUpdateCache.latestManifestAutoUpdated;
            if (left > 60*60*1000) {
                Thread thread = new Thread(() -> {
                    String remoteManifest = null;
                    try {
                        remoteManifest = NetworkUtil.parseTextPage(SharedConstrains.VERSION_MANIFEST_V2);
                        Manifest manifest = app.getGson().fromJson(remoteManifest, Manifest.class);
                        runOnUiThread(() -> {
                            app.getScheduleInformatorApp().sendNotify(10, new NotificationCompat.Builder(SchoolGuideService.this, ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NONE)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(manifest.latest.release.getChangelog())
                                    .build());
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                autoUpdateCache.latestManifestAutoUpdated = System.currentTimeMillis();
            }

            manifestUpdaterHandler.postDelayed(manifestUpdaterRunnable, 5000);
        };
    }

    public void runOnUiThread(Runnable r) {
        r.run();
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