package ru.fazziclay.schoolguide.app.global;

import com.google.gson.Gson;

import java.io.File;

import ru.fazziclay.schoolguide.app.MilkLog;
import ru.fazziclay.schoolguide.app.SharedConstrains;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.util.DataUtil;
import ru.fazziclay.schoolguide.util.FileUtil;
import ru.fazziclay.schoolguide.util.NetworkUtil;

/**
 * <h1>Менеджер глобальных данных</h1>
 *
 * <h2>Глобальные данные</h2>
 * <p>Глобальные данные - это данные которые нахотятся на удалённом сервере(raw.githubusercontent.com) и для
 * оптимизации хранят локальную копию в кеше</p>
 * <p>Среди этих данных есть Манифест Версий и Встроенный Список Пресетов</p>
 *
 * <h2>Особенности</h2>
 * <p>Из за особенностей андроида в главном потоке мы не может работать с сетью, есть 2 функции, которые различаются лишь выполнением
 * в отдельнм потоке</p>
 * **/
public class GlobalManager {
    private static final String CACHE_LOCAL_VERSION_MANIFEST = "local.versionManifest.json";
    private static final String CACHE_LOCAL_BUILTIN_SCHEDULE = "local.builtinSchedule.json";
    private static final String THREAD_NAME = "GlobalManager-getInExternalThread";
    private static final String EXCEPTION_NULLPOINTEREXCEPTION_APP = "app is null!";
    private static final String EXCEPTION_NULLPOINTEREXCEPTION_INTERFACE = "interface is null!";
    private static final String EXCEPTION_NULLPOINTEREXCEPTION_TO_FAILED = "versionManifest or builtinSchedule is null!";

    /**
     * Получить в отдельнм потоке(создать) и выдать в globalManagerInterface
     * **/
    public static void getInExternalThread(SchoolGuideApp app, ResponseInterface responseInterface) {
        if (app == null) throw new NullPointerException(EXCEPTION_NULLPOINTEREXCEPTION_APP);
        if (responseInterface == null) throw new NullPointerException(EXCEPTION_NULLPOINTEREXCEPTION_INTERFACE);

        Thread thread = new Thread(() -> getInCurrentThread(app, true, responseInterface));
        thread.setName(THREAD_NAME);
        thread.start();
    }

    public static void getInCurrentThread(SchoolGuideApp app, boolean onlineMode, ResponseInterface responseInterface) {
        if (app == null) throw new NullPointerException(EXCEPTION_NULLPOINTEREXCEPTION_APP);
        if (responseInterface == null) throw new NullPointerException(EXCEPTION_NULLPOINTEREXCEPTION_INTERFACE);

        try {
            Gson gson = app.getGson();
            File cacheDir = app.getCacheDir();

            File localVersionManifestFile = new File(cacheDir, CACHE_LOCAL_VERSION_MANIFEST);
            File localBuiltinScheduleFile = new File(cacheDir, CACHE_LOCAL_BUILTIN_SCHEDULE);

            String keysStr;
            GlobalCacheKeys globalCacheKeys = null;
            if (onlineMode) {
                keysStr = NetworkUtil.parseTextPage(SharedConstrains.API_LOCAL_CACHE_KEYS_URL);
                globalCacheKeys = gson.fromJson(keysStr, GlobalCacheKeys.class);
            }

            GlobalLatestVersionManifest latestVersionManifest = DataUtil.load(localVersionManifestFile, GlobalLatestVersionManifest.class);
            GlobalBuiltinPresetList builtinPresetList = DataUtil.load(localBuiltinScheduleFile, GlobalBuiltinPresetList.class);

            if (onlineMode && globalCacheKeys.getLatestVersionManifestKey() != latestVersionManifest.getLocalCacheKay()) {
                String versionManifestStr = NetworkUtil.parseTextPage(SharedConstrains.API_LATEST_VERSION_MANIFEST_URL);
                latestVersionManifest = gson.fromJson(versionManifestStr, GlobalLatestVersionManifest.class);
                FileUtil.write(localVersionManifestFile, versionManifestStr);
            }

            if (onlineMode && globalCacheKeys.getBuiltinPresetListKey() != builtinPresetList.getLocalCacheKay()) {
                String builtinScheduleStr = NetworkUtil.parseTextPage(SharedConstrains.API_BUILTIN_PRESET_LIST_URL);
                builtinPresetList = gson.fromJson(builtinScheduleStr, GlobalBuiltinPresetList.class);
                FileUtil.write(localBuiltinScheduleFile, builtinScheduleStr);
            }

            if (latestVersionManifest == null || builtinPresetList == null) {
                throw new NullPointerException(EXCEPTION_NULLPOINTEREXCEPTION_TO_FAILED);
            }

            responseInterface.success(globalCacheKeys, latestVersionManifest, builtinPresetList);
        } catch (Exception e) {
            MilkLog.g("GlobalManager: failed! (processed by interface!)", e);
            responseInterface.failed(e);
        }
    }

    /**
     * Интерфейс получения глобальных данных
     * **/
    public interface ResponseInterface {
        /**
         * Если не удалось
         * **/
        void failed(Exception exception);

        void success(GlobalCacheKeys keys, GlobalLatestVersionManifest versionManifest, GlobalBuiltinPresetList builtinSchedule);
    }
}
