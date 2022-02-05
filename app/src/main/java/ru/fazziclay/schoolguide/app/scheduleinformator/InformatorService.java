package ru.fazziclay.schoolguide.app.scheduleinformator;

import static ru.fazziclay.schoolguide.util.Crutches.appInitializationDelay;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class InformatorService extends Service {
    private ScheduleInformatorApp scheduleInformatorApp;

    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        appInitializationDelay(SharedConstrains.CRUTCH_INIT_DELAY);
        SchoolGuideApp app = SchoolGuideApp.get(this);
        scheduleInformatorApp = app.getScheduleInformatorApp();
        scheduleInformatorApp.registerService(this);

        handler = new Handler(Looper.myLooper());
        runnable = () -> handler.postDelayed(runnable, scheduleInformatorApp.tick());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        scheduleInformatorApp.onServiceDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}