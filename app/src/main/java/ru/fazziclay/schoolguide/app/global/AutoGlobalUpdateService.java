package ru.fazziclay.schoolguide.app.global;

import static ru.fazziclay.schoolguide.util.Crutches.appInitializationDelay;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class AutoGlobalUpdateService extends Service {
    private static final int UPDATE_DELAY = 60 * 60 * 1000;

    private SchoolGuideApp app;
    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        appInitializationDelay(SharedConstrains.CRUTCH_INIT_DELAY);
        app = SchoolGuideApp.get(this);
        if (app == null) {
            Log.e("ERROR", "AutoGlobalUpdateService: app is null! stopSelf");
            stopSelf();
            return;
        }

        handler = new Handler(getMainLooper());
        runnable = () -> {
            update(app);
            handler.postDelayed(runnable, UPDATE_DELAY);
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void update(SchoolGuideApp app) {
        GlobalManager.get(app, new GlobalManager.GlobalManagerInterface() {
            @Override
            public void failed(Exception exception) {}

            @Override
            public void success(GlobalKeys keys, GlobalVersionManifest versionManifest, GlobalBuiltinPresetList builtinSchedule) {
                app.setGlobalVersionManifest(versionManifest);
                app.setGlobalBuiltinPresetList(builtinSchedule);
                app.getGlobalUpdateCallbacks().run((callbackStorage, callback) -> callback.onGlobalUpdate(keys, versionManifest, builtinSchedule));
            }
        });
    }
}