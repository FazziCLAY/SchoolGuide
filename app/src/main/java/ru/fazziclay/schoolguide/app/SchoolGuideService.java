package ru.fazziclay.schoolguide.app;

import static ru.fazziclay.schoolguide.app.UpdateCheckerService.speedCrutch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SchoolGuideService extends Service {

    @Override
    public void onCreate() {
        speedCrutch();
        SchoolGuideApp.get(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}