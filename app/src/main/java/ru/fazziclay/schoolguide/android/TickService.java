package ru.fazziclay.schoolguide.android;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import ru.fazziclay.schoolguide.SchoolGuide;

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
            short nextDelay = app.tick();
            loopHandler.postDelayed(loopRunnable, nextDelay);
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