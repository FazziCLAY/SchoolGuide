package ru.fazziclay.schoolguide.datafixer;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.util.FileUtil;

public class DataFixer {
    public static final AbstractScheme[] SCHEMES = new AbstractScheme[] {
            new AbstractScheme() {
                @Override
                public boolean isCompatible(int version) {
                    return version <= 33;
                }

                @Override
                public int run(int version) {
                    return 35;
                }
            }
    };

    Gson gson;
    Context context;
    int currentVersion;

    public DataFixer(Context context) {
        this.context = context;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
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
                        currentVersion = scheme.run(currentVersion);
                    } catch (Exception ignored) {}
                }
            }
            i++;
        }
    }

    public int tryGetVersion() {
        File externalFilesDir = context.getExternalFilesDir(null);
        File FILE_VERSION = new File(externalFilesDir, "version");
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
            SettingsV33 settingsV33;
            try {
                settingsV33 = gson.fromJson(FileUtil.read(FILE_SETTINGS_V33.getAbsolutePath(), "{}"), SettingsV33.class);
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

    public static class SettingsV33 {
        int version = -1;
        List<Integer> versionsHistory;
    }
}
