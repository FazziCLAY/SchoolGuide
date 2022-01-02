package ru.fazziclay.schoolguide.datafixer.schema;

import android.content.Context;

import java.io.File;

import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.util.FileUtil;

public class SchemaV33OLD extends Schema {
    @Override
    public boolean isCompatibly(int version) {
        return version <= 33;
    }

    @Override
    public int run() {
        Context context = SchoolGuide.get().getApplicationContext();
        File externalHome = context.getExternalFilesDir(null);
        File externalCache = context.getExternalCacheDir();

        renameSettings(externalHome);
        renameSchedule(externalHome);

        reloadManifest(externalCache);
        reloadStateCache(externalCache);

        return 34;
    }

    void renameSettings(File home) {
        File oldSettingsFile = new File(home, "settings.json");
        if (oldSettingsFile.exists()) {
            FileUtil.write(home.getAbsolutePath() + "/" + "schoolguide.settings.json", FileUtil.read(oldSettingsFile.getAbsolutePath()));
        }
        oldSettingsFile.delete();
    }

    void renameSchedule(File home) {
        File oldScheduleFile = new File(home, "schedule.json");
        if (oldScheduleFile.exists()) {
            FileUtil.write(home.getAbsolutePath() + "/" + "schoolguide.schedule.json", FileUtil.read(oldScheduleFile.getAbsolutePath()));
        }
        oldScheduleFile.delete();
    }

    void reloadManifest(File cache) {
        File oldManifestFile = new File(cache, "manifest.json");
        if (oldManifestFile.exists()) {
            oldManifestFile.delete();
        }
    }

    void reloadStateCache(File cache) {
        File oldStateCacheFile = new File(cache, "state_cache.json");
        if (oldStateCacheFile.exists()) {
            oldStateCacheFile.delete();
        }
    }
}
