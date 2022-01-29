package ru.fazziclay.schoolguide.datafixer;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;

import ru.fazziclay.schoolguide.util.AppTrace;
import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.util.FileUtil;

/**
 * <H1>DataFixer</H1>
 * <H2>Исправляет данные старых версий и делает их читаемыми для новой</H2>
 *
 * <p>Требуется создать объект, передать в него нужно андроид контекст, текущую версию приложения, и схему востановления({@link AbstractScheme})</p>
 * <p>Для определения использует файл version.json в data/data</p>
 *
 * @see AbstractScheme
 * @see Version
 * **/
public class DataFixer {
    private static final int PRE36_VERSION = -36;

    private final Context context;
    private final int currentAppVersion;
    private final AbstractScheme[] fixSchemes;
    private final Gson gson;
    private final File versionFile;
    private Version version;
    private final AppTrace appTrace;

    public DataFixer(AppTrace appTrace, Context context, int currentAppVersion, AbstractScheme[] fixSchemes) {
        appTrace.point("dataFixer init");
        this.appTrace = appTrace;
        this.context = context;
        this.currentAppVersion = currentAppVersion;
        this.fixSchemes = fixSchemes;
        this.gson = new Gson();
        this.versionFile = new File(context.getFilesDir(), "version.json");
        boolean isPre36 = (context.getExternalFilesDir("").list().length > 0 && !versionFile.exists());
        try {
            this.version = gson.fromJson(
                    FileUtil.read(versionFile, "{}"),
                    Version.class
            );
        } catch (Exception e) {
            appTrace.point("exception while version file parse", e);
            this.version = Version.createNone();
        }
        if (version.getFirstVersion() == 0) version.setFirstVersion(isPre36 ? PRE36_VERSION : currentAppVersion);
        if (version.getLatestVersion() == 0) version.setLatestVersion(isPre36 ? PRE36_VERSION : currentAppVersion);
        if (isPre36) {
            version.addToVersionHistory(PRE36_VERSION);
        }
        version.addToVersionHistory(currentAppVersion);
        saveVersion();
    }

    /**
     * Сохранить файл версии (version.json)
     * **/
    public void saveVersion() {
        appTrace.point("DataFixer saveVersion");
        FileUtil.write(versionFile, gson.toJson(version, Version.class));
    }

    public void fixIfAvailable() {
        appTrace.point("DataFixer fixIfAvailable");
        if (version.getLatestVersion() >= currentAppVersion) {
            return;
        }

        int i = 0;
        int o = fixSchemes.length;
        while (i < o) {
            Log.d("DataFixer", "fixIfAvailable(); while i="+i+" o="+o);
            AbstractScheme fixScheme = fixSchemes[i];

            try {
                if (fixScheme.isCompatible(version)) {
                    version = fixScheme.run(this, version);
                    i = -1;
                }
            } catch (Exception e) {
                appTrace.point("exception while run scheme "+fixScheme.getClass().getName()+" i="+i, e);
            }

            i++;
        }

        version.setLatestVersion(currentAppVersion);
        saveVersion();
    }

    public Context getAndroidContext() {
        return context;
    }

    public AppTrace getAppTrace() {
        return appTrace;
    }

    public Gson getGson() {
        return gson;
    }
}
