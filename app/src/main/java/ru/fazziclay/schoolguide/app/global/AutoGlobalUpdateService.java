package ru.fazziclay.schoolguide.app.global;

import static ru.fazziclay.schoolguide.util.Crutches.appInitializationDelay;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.callback.CallbackStorage;
import ru.fazziclay.schoolguide.callback.GlobalUpdateListener;

public class AutoGlobalUpdateService extends Service {
    private SchoolGuideApp app;
    private CallbackStorage<GlobalUpdateListener> callbacks;
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
        callbacks = app.getGlobalUpdateCallbacks();

        handler = new Handler(getMainLooper());
        runnable = () -> {
            GlobalManager.get(this, new GlobalManager.GlobalManagerInterface() {
                @Override
                public void failed(Exception exception) {
                    callbacks.run((callbackStorage, callback) -> callback.onGlobalUpdate(exception, null, null, null));
                }

                @Override
                public void success(GlobalKeys keys, GlobalVersionManifest versionManifest, GlobalBuiltinPresetList builtinSchedule) {
                    app.setGlobalVersionManifest(versionManifest);
                    app.setGlobalBuiltinPresetList(builtinSchedule);
                    callbacks.run((callbackStorage, callback) -> callback.onGlobalUpdate(null, keys, versionManifest, builtinSchedule));
                }
            });
            handler.postDelayed(runnable, 60 * 60 * 1000);
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
}