package ru.fazziclay.schoolguide.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.app.global.AutoGlobalUpdateService;
import ru.fazziclay.schoolguide.app.global.GlobalBuiltinPresetList;
import ru.fazziclay.schoolguide.app.global.GlobalVersionManifest;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.callback.CallbackStorage;
import ru.fazziclay.schoolguide.callback.GlobalUpdateListener;
import ru.fazziclay.schoolguide.callback.Status;
import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.util.AppTrace;
import ru.fazziclay.schoolguide.util.DataUtil;
import ru.fazziclay.schoolguide.util.FileUtil;

/**
 * <H1>Главный класс приложения</H1>
 * В конструкторе он всё инициализирует, создаёт и запускает
 *
 * <H2>Получение</H2>
 * <p>Обьект получается статическими методами {@link SchoolGuideApp#get()} и {@link SchoolGuideApp#get(Context)}</p>
 * <p>В случае если обьекта нету в переменной {@link SchoolGuideApp#instance} обьект будет создан => всё запустится</p>
 * <p>но только если мы передали контекст в функцию get(), а если нет то приложение выдаст RuntimeException с cause NullPointerException</p>
 *
 *
 * **/
public class SchoolGuideApp {
    /**
     * Инстанс, нужен для того что бы каждый раз не создавать обьект
     * @see SchoolGuideApp
     * **/
    public static SchoolGuideApp instance = null;

    /**
     * Доступен ли инстанс приложения
     * **/
    public static boolean isInstanceAvailable() {
        return instance != null;
    }

    /**
     * Выдаёт инстанс приложения, если его в переменной {@link SchoolGuideApp#instance} нету, то создаст
     * @return Инстанс приложения, если ошибка при создании то null
     * **/
    @Nullable
    public static SchoolGuideApp get(Context context) {
        if (!isInstanceAvailable()) {
            try {
                instance = new SchoolGuideApp(context);
            } catch (Exception e) {
                Log.e("SchoolGuideApp#get()", "При создании обьекта приложения, он выдат ошибку", e);
                return null;
            }
        }
        return instance;
    }

    /**
     * @see SchoolGuideApp#get(Context)
     * **/
    @Nullable
    public static SchoolGuideApp get() {
        return get(null);
    }


    /**
     * Трейсер приложения, для понимания что вообще происходит
     * @see AppTrace
     * **/
    private final AppTrace appTrace;

    /**
     * Контекст андроид приложения
     * нужен для всяких андроид штук
     * **/
    private final Context androidContext;

    /**
     * <p>Дерриктория файлов программы, находится обычно в .../Android/data/(package)/files/</p>
     * <p>Тут нужно хранить ценные "Влажные" файлы</p>
     * **/
    private final File filesDir;

    /**
     * <p>Дерриктория кеша программы, находится обычно в .../Android/data/(package)/cache/</p>
     * <p>Тут можно хранить временные файлы, ну файлы кеша кароче</p>
     * **/
    private final File cacheDir;

    /**
     * <p>Файл настроек, наследник {@link SchoolGuideApp#filesDir}</p>
     * <p>Должен быть settings.json</p>
     * **/
    private final File settingsFile;

    /**
     * <p>Gson для работы с json, очень желательно везде где он нужен, брать его именно от сюда</p>
     * <p>И оперативы меньше, и если надо PrettyPrint то изи будет сделать</p>
     * **/
    private final Gson gson;

    /**
     * Настройки приложения, из можно менять через этот обьект, а сохранять их на случай перезагрузки
     * надо функциец {@link SchoolGuideApp#saveSettings()}
     * **/
    private final Settings settings;

    /**
     * Приложение "Информатор Расписания"
     * Там находится всё то что отвечает за распорядок дня так сказать
     * **/
    private final ScheduleInformatorApp scheduleInformatorApp;

    /**
     * <p>Доступно ли обновление</p>
     * <p>для (!) в интерфейсе приложения перед надписью центра обновлений</p>
     * **/
    private boolean isUpdateAvailable = false;


    /**
     * Последий манифест версий, обновляется автоматически
     * @see AutoGlobalUpdateService
     * @see ru.fazziclay.schoolguide.app.global.GlobalManager
     * @see GlobalVersionManifest
     * **/
    private GlobalVersionManifest globalVersionManifest;

    /**
     * Последний встроенный список расписаний
     * @see AutoGlobalUpdateService
     * @see ru.fazziclay.schoolguide.app.global.GlobalManager
     * @see GlobalBuiltinPresetList
     * **/
    private GlobalBuiltinPresetList globalBuiltinPresetList;

    /**
     * Callback хранилеще для авто обновлений глобальных данных
     * @see AutoGlobalUpdateService
     * **/
    private final CallbackStorage<GlobalUpdateListener> globalUpdateCallbacks = new CallbackStorage<>();

    private final Thread saveAppTraceThread = new Thread(() -> {
        while (true) {
            saveAppTrace();
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException ignored) {}
        }
    });


    public SchoolGuideApp(Context context) {
        if (context == null) {
            throw new RuntimeException("Failed to create SchoolGuideApp", new NullPointerException("context is null!"));
        }
        appTrace = new AppTrace("SchoolGuideApp <init>");
        androidContext = context.getApplicationContext();
        gson = new Gson();

        // До этого этапа мы не работали с данными, Он исправил все файлы старых версий и сделает их читаемыми для новой
        DataFixer dataFixer = new DataFixer(appTrace, androidContext, SharedConstrains.APPLICATION_VERSION_CODE, SharedConstrains.DATA_FIXER_SCHEMES);
        dataFixer.fixIfAvailable();

        filesDir = context.getExternalFilesDir(null);
        cacheDir = context.getExternalCacheDir();

        settingsFile = new File(filesDir, "settings.json");
        settings = DataUtil.load(settingsFile, Settings.class);

        saveSettings();

        registerCallbacks();

        androidContext.startService(new Intent(androidContext, SchoolGuideService.class));
        androidContext.startService(new Intent(androidContext, AutoGlobalUpdateService.class));

        scheduleInformatorApp = new ScheduleInformatorApp(this);

        saveAppTraceThread.setDaemon(true);
        saveAppTraceThread.start();
    }

    private void registerCallbacks() {
        globalUpdateCallbacks.addCallback((exception, globalKeys, globalVersionManifest, globalBuiltinPresetList) -> {
            if (exception == null) {
                setUpdateAvailable(globalVersionManifest != null && globalVersionManifest.latestVersion != null && globalVersionManifest.latestVersion.getCode() > SharedConstrains.APPLICATION_VERSION_CODE);
            }

            return new Status.Builder()
                    .setDeleteCallback(false)
                    .build();
        });
    }

    /**
     * Регистрирует каналы уведомлений согласно схеме в {@link SharedConstrains}
     * @apiNote android >= Build.VERSION_CODES.O
     * **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void registerNotificationChannels(Context context) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        List<NotificationChannel> channels = SharedConstrains.getNotificationChannels(context);

        for (NotificationChannel channel : channels) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Сохраняет данные настроек в файл
     * @see SchoolGuideApp#settings
     * @see SchoolGuideApp#settingsFile
     * **/
    public void saveSettings() {
        DataUtil.save(settingsFile, settings);
    }

    public void saveAppTrace() {
        try {
            FileUtil.write(new File(androidContext.getExternalCacheDir(), "latestAppTrace.txt"), appTrace.getText());
        } catch (Exception e) {
            Log.e("saveAppTrace", "error while saving appTrace", e);
        }
    }

    /**
     * @see SchoolGuideApp#appTrace
     * **/
    public AppTrace getAppTrace() {
        return appTrace;
    }

    /**
     * @see SchoolGuideApp#androidContext
     * **/
    public Context getAndroidContext() {
        return androidContext;
    }

    /**
     * @see SchoolGuideApp#cacheDir
     * **/
    public File getCacheDir() {
        return cacheDir;
    }

    /**
     * @see SchoolGuideApp#filesDir
     * **/
    public File getFilesDir() {
        return filesDir;
    }

    /**
     * @see SchoolGuideApp#gson
     * **/
    public Gson getGson() {
        return gson;
    }

    /**
     * @see SchoolGuideApp#settingsFile
     * **/
    public File getSettingsFile() {
        return settingsFile;
    }

    /**
     * @see SchoolGuideApp#settings
     * **/
    public Settings getSettings() {
        return settings;
    }

    /**
     * @see SchoolGuideApp#scheduleInformatorApp
     * **/
    public ScheduleInformatorApp getScheduleInformatorApp() {
        return this.scheduleInformatorApp;
    }

    /**
     * @see SchoolGuideApp#isUpdateAvailable
     * **/
    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    /**
     * @see SchoolGuideApp#isUpdateAvailable
     * **/
    public void setUpdateAvailable(boolean updateAvailable) {
        isUpdateAvailable = updateAvailable;
    }

    public GlobalVersionManifest getGlobalVersionManifest() {
        return globalVersionManifest;
    }

    public void setGlobalVersionManifest(GlobalVersionManifest globalVersionManifest) {
        this.globalVersionManifest = globalVersionManifest;
    }

    public GlobalBuiltinPresetList getGlobalBuiltinPresetList() {
        return globalBuiltinPresetList;
    }

    public void setGlobalBuiltinPresetList(GlobalBuiltinPresetList globalBuiltinPresetList) {
        this.globalBuiltinPresetList = globalBuiltinPresetList;
    }

    public CallbackStorage<GlobalUpdateListener> getGlobalUpdateCallbacks() {
        return globalUpdateCallbacks;
    }
}
