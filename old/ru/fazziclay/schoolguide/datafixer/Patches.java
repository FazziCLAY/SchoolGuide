package ru.fazziclay.schoolguide.datafixer;

import android.app.NotificationManager;
import android.content.Context;

import java.io.File;
import java.util.List;

import ru.fazziclay.schoolguide.SharedConstrains;

public class Patches {
    /*

    // Патч уровня б стартует после создания SchoolGuide
    // generic потаму что от может включить другие патчи которым мужно знать версии
    private void patch_b_2021_12_22_v33_generic() {
        List<Integer> versionHistory = app.getSettingsProvider().getVersionsHistory();
        if (!versionHistory.contains(SharedConstrains.APPLICATION_VERSION_CODE)) {
            int latestVersion = -1;
            for (int ver : versionHistory) {
                if (latestVersion < ver) latestVersion = ver;
            }

            patch_ba_2021_12_22_v33();
        }
    }

    // ba ответвление от b но в категории b оно a (хз почему может будет bb или bc)
    // при обновлении удаляет папку кеша
    private void patch_ba_2021_12_22_v33() {
        File cache = getExternalCacheDir();
        try {
            if (cache.exists()) cache.delete();
        } catch (Exception ignored) {}
    }

    // Удалить каналы уведомлений старых версий
    private void patch_a_2021_11_29_v31() {
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
    private void patch_a_2021_11_19_v25() {
        File f = new File(getExternalFilesDir(null), "manifest.json");
        f.delete();
    }*/
}
