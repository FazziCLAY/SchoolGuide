package ru.fazziclay.schoolguide.datafixer;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33Settings;
import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.datafixer.schem.v33to35.Scheme33To35;
import ru.fazziclay.schoolguide.util.FileUtil;

public class DataFixer {
    public static final AbstractScheme[] SCHEMES = new AbstractScheme[] {
            new Scheme33To35()
    };

    SchoolGuideApp app;

    File FILE_VERSION;

    Gson gson;
    Context context;
    int currentVersion;

    public DataFixer(SchoolGuideApp app) {
        this.app = app;
        this.context = app.getAndroidContext();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        FILE_VERSION = new File(context.getExternalFilesDir(null), "version");
    }

    public void tryFix() {
        try {
            this.currentVersion = tryGetVersion();
        } catch (Exception e) {
            this.currentVersion = -1;
        }

        int i = 0;
        while (i < 55) {
            for (AbstractScheme scheme : SCHEMES) {
                if (scheme.isCompatible(currentVersion)) {
                    try {
                        currentVersion = scheme.run(context, currentVersion);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            i++;
        }

        FileUtil.write(FILE_VERSION.getAbsolutePath(), String.valueOf(currentVersion));
    }

    public int tryGetVersion() {
        File externalFilesDir = context.getExternalFilesDir(null);
        File FILE_SETTINGS_V33 = new File(externalFilesDir, "settings.json");

        String[] filesList = externalFilesDir.list();
        if (filesList == null) {
            return SharedConstrains.APPLICATION_VERSION_CODE;
        }

        if (filesList.length == 0) {
            return SharedConstrains.APPLICATION_VERSION_CODE;
        }

        if (FILE_VERSION.exists()) {
            String vString = FileUtil.read(FILE_VERSION.getAbsolutePath(), "default");
            int v = -1;
            try {
                v = Integer.parseInt(vString);
            } catch (Exception ignored) {}
            return v;
        }

        if (FILE_SETTINGS_V33.exists()) {
            V33Settings settingsV33;
            try {
                settingsV33 = gson.fromJson(FileUtil.read(FILE_SETTINGS_V33.getAbsolutePath(), "{}"), V33Settings.class);
                if (settingsV33 != null) {
                    if (settingsV33.versionsHistory != null && !settingsV33.versionsHistory.isEmpty()) {
                        int max = 0;
                        for (int i : settingsV33.versionsHistory) {
                            if (i > max) max = i;
                        }
                        return max;
                    }
                }
            } catch (Exception ignored) {}
        }

        return -1;
    }
}
