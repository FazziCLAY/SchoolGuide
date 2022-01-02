package ru.fazziclay.schoolguide.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.SharedConstrains;

public class TickService extends Service {
    private SchoolGuide app = null;

    public Handler loopHandler = null;
    public Runnable loopRunnable = null;

    @Override
    public void onCreate() {
        app = SchoolGuide.get(this);
        app.registerTickServiceInstance(this);

        loopHandler = new Handler(Looper.myLooper());
        loopRunnable = () -> {
            app.tick();
            loopHandler.postDelayed(loopRunnable, SharedConstrains.LOOP_DELAY);
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loopHandler.post(loopRunnable);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}