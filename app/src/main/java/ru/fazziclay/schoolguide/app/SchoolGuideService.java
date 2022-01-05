package ru.fazziclay.schoolguide.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SchoolGuideService extends Service {
    SchoolGuideApp app;

    @Override
    public void onCreate() {
        app = SchoolGuideApp.get(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}