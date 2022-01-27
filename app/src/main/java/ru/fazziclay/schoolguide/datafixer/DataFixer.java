package ru.fazziclay.schoolguide.datafixer;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;

import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.util.FileUtil;

public class DataFixer {
    private static final int PRE36_VERSION = -36;

    private final Context context;
    private final int currentAppVersion;
    private final AbstractScheme[] fixSchemes;
    private final Gson gson;
    private final File versionFile;
    private Version version;

    public DataFixer(Context context, int currentAppVersion, AbstractScheme[] fixSchemes) {
        Log.d("DataFixer", "<init>");
        this.context = context;
        this.currentAppVersion = currentAppVersion;
        this.fixSchemes = fixSchemes;
        this.gson = new Gson();
        this.versionFile = new File(context.getFilesDir(), "version.json");
        boolean isPre36 = (context.getExternalFilesDir("").list().length > 0 && !versionFile.exists());
        this.version = gson.fromJson(
                FileUtil.read(versionFile, "{}"),
                Version.class
        );
        if (version.getFirstVersion() == 0) version.setFirstVersion(isPre36 ? PRE36_VERSION : currentAppVersion);
        if (version.getLatestVersion() == 0) version.setLatestVersion(isPre36 ? PRE36_VERSION : currentAppVersion);
        if (isPre36) {
            version.addToVersionHistory(PRE36_VERSION);
        }
        version.addToVersionHistory(currentAppVersion);
        saveVersion();
    }

    public void saveVersion() {
        FileUtil.write(versionFile, gson.toJson(version, Version.class));
    }

    public void fixIfAvailable() {
        Log.d("DataFixer", "fixIfAvailable();");
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
                e.printStackTrace();
            }

            i++;
        }

        saveVersion();
    }

    public Context getAndroidContext() {
        return context;
    }
}
