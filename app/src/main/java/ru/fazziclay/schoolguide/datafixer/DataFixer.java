package ru.fazziclay.schoolguide.datafixer;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.util.FileUtil;

public class DataFixer {
    private static final int PRE36_VERSION = -36;

    Context context;
    int currentAppVersion;
    AbstractScheme[] fixSchemes;
    Gson gson;
    File versionFile;
    Version version;

    public DataFixer(Context context, int currentAppVersion, AbstractScheme[] fixSchemes) {
        this.context = context;
        this.currentAppVersion = currentAppVersion;
        this.fixSchemes = fixSchemes;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.versionFile = new File(context.getFilesDir(), "version.json");
        boolean isPre36 = (context.getExternalFilesDir("").list().length > 0 && !versionFile.exists());
        this.version = gson.fromJson(
                FileUtil.read(versionFile, "{}"),
                Version.class
        );
        if (version.firstVersion == 0) version.firstVersion = (isPre36 ? PRE36_VERSION : currentAppVersion);
        if (version.latestVersion == 0) version.latestVersion = currentAppVersion;
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
        if (version.latestVersion >= currentAppVersion) {
            return;
        }

        int i = 0;
        int o = fixSchemes.length;
        while (i < o) {
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
    }
}
