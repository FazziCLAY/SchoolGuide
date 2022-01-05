package ru.fazziclay.schoolguide.app.scheduleinformator;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class InformatorService extends Service {
    public SchoolGuideApp app;
    public ScheduleInformatorApp scheduleInformatorApp;

    public Handler handler;
    public Runnable runnable;

    @Override
    public void onCreate() {
        app = SchoolGuideApp.get(this);
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