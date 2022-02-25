package ru.fazziclay.schoolguide.app.scheduleinformator;

import static ru.fazziclay.schoolguide.util.Crutches.appInitializationDelay;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import ru.fazziclay.schoolguide.app.MilkLog;
import ru.fazziclay.schoolguide.app.SharedConstrains;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class InformatorService extends Service {
    private static final int MAX_REPORTS_ERRORS = 10;

    private ScheduleInformatorApp scheduleInformatorApp;

    private Handler handler;
    private Runnable runnable;
    private int reportedErrors = 0;

    @Override
    public void onCreate() {
        appInitializationDelay(SharedConstrains.CRUTCH_INIT_DELAY);
        SchoolGuideApp app = SchoolGuideApp.get(this);
        if (app == null) {
            stopSelf();
            return;
        }
        scheduleInformatorApp = app.getScheduleInformatorApp();
        scheduleInformatorApp.registerService(this);

        handler = new Handler(Looper.myLooper());
        runnable = () -> {
            try {
                handler.postDelayed(runnable, scheduleInformatorApp.tick());
            } catch (Exception e) {
                if (reportedErrors <= MAX_REPORTS_ERRORS) {
                    MilkLog.g("Exception while run scheduleInformatorApp.tick()", e);
                    app.sendErrorNotification();
                }
                reportedErrors++;
                handler.postDelayed(runnable, 3000);
            }
        };
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