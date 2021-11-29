package ru.fazziclay.schoolguide.android.activity;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.android.service.ForegroundService;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            try {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
            } catch (Throwable ignored) {}

            // Патчи
            try {
                patch_2021_11_19_v25();
                patch_2021_11_29_v31();
            } catch (Exception ignored) {}

            if (!SchoolGuide.isInstanceAvailable()) {
                new SchoolGuide(this);
            }

            SchoolGuide.getInstance().getSettingsProvider().addVersionsHistory(SharedConstrains.APPLICATION_VERSION_CODE);
            SchoolGuide.getInstance().updateManifestTick(true);

            startService(new Intent(this, ForegroundService.class));
            startActivity(new Intent(this, HomeActivity.class));
            finish();

        } catch (Throwable throwable) {
            new CrashReport(this, throwable);
        }
    }

    // Удалить каналы уведомлений старых версий
    private void patch_2021_11_29_v31() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String[] toDelete = new String[] {"UpdateChecker", "Foreground", "CrashReport", "External"};

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            for (String channel : toDelete) {
                notificationManager.deleteNotificationChannel(channel);
            }
        }
    }

    // Старая версия не поддерживала developerSchedule а сохраняла она не удалённый файл а его дересиализованную сериализацию (удалённый файл -> объект -> файл на устройстве)
    // из за этого developerSchedule Null а загружатся с сервера не желает так как manifest.key
    //
    // Суть патча отследить в state_cache.json первую версию установки(по названию послеюную использованную, но на деле там версия первого запуска)
    // ИТОГ: Файл manifest.json переезжает в папку с кешем а из папки данных мы его удаляем в этом патче если версия ниже версии написания этого патча x < 25
    private void patch_2021_11_19_v25() {
        File f = new File(getExternalFilesDir(null), "manifest.json");
        f.delete();
    }
}