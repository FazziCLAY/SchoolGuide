package ru.fazziclay.schoolguide.datafixer.schem.v33to35;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.UUID;

import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.datafixer.Version;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33Lesson;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33LessonInfo;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33LocalSchedule;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33Schedule;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33Settings;
import ru.fazziclay.schoolguide.datafixer.old.v37.V37AppSchedule;
import ru.fazziclay.schoolguide.datafixer.old.v37.V37Event;
import ru.fazziclay.schoolguide.datafixer.old.v37.V37EventInfo;
import ru.fazziclay.schoolguide.datafixer.old.v37.V37Preset;
import ru.fazziclay.schoolguide.datafixer.old.v37.V37Settings;
import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.util.AppTrace;
import ru.fazziclay.schoolguide.util.FileUtil;

/**
 * <p>Востанавливает данные версий до v36 (0.3) до версии v37</p>
 *
 * <p>Удаляет старый манифест(он брался с гитхаба и клался в кеш)</p>
 * <p>Удаляет старый state_cache(кеш стадий)</p>
 *
 * <p>Востанавливает расписание schedule.json -> scheduleinformator.schedule.json</p>
 * <p>Востанавливает настройки settings.json -> settings.json</p>
 *
 * @see DataFixer
 * @see AbstractScheme
 * **/
public class SchemePre36To37 extends AbstractScheme {
    final String OLD_MANIFEST_FILE = "manifest.json";
    final String OLD_STATECACHE_FILE = "state_cache.json";
    final String OLD_SCHEDULE_FILE = "schedule.json";
    final String OLD_SETTINGS_FILE = "settings.json";

    final String NEW_SCHEDULE_FILE = "scheduleinformator.schedule.json";
    final String NEW_SETTINGS_FILE = "settings.json";

    AppTrace appTrace;
    Gson gson;

    Context context;
    File cacheDir;
    File filesDir;

    File oldManifestFile;
    File oldStateCacheFile;
    File oldScheduleFile;
    File oldSettingsFile;

    File newScheduleFile;
    File newSettingsFile;

    V33Schedule oldSchedule;
    V37AppSchedule newSchedule;

    V33Settings oldSettings;
    V37Settings newSettings;

    @Override
    public boolean isCompatible(Version version) {
        Log.d("SchemePre36To36", "isCompatible = "+version.getLatestVersion());
        return version.getLatestVersion() < 36;
    }

    @Override
    public Version run(DataFixer dataFixer, Version version) {
        appTrace = dataFixer.getAppTrace();
        gson = dataFixer.getGson();
        context = dataFixer.getAndroidContext();
        cacheDir = context.getExternalCacheDir();
        filesDir = context.getExternalFilesDir(null);

        oldManifestFile = new File(cacheDir, OLD_MANIFEST_FILE);
        oldStateCacheFile = new File(cacheDir, OLD_STATECACHE_FILE);
        oldScheduleFile = new File(filesDir, OLD_SCHEDULE_FILE);
        oldSettingsFile = new File(filesDir, OLD_SETTINGS_FILE);

        newScheduleFile = new File(filesDir, NEW_SCHEDULE_FILE);
        newSettingsFile = new File(filesDir, NEW_SETTINGS_FILE);

        try {
            fix();
        } catch (Exception e) {
            appTrace.point("Error in fix(what?)", e);
        }

        version.setLatestVersion(36);
        return version;
    }

    private void fix() {
        Log.d("SchemePre36To36", "fix");
        try {
            if (oldManifestFile.exists()) oldManifestFile.delete();
        } catch (Exception e) {
            appTrace.point("delete oldManifestFile", e);
        }

        try {
            if (oldStateCacheFile.exists()) oldStateCacheFile.delete();
        } catch (Exception e) {
            appTrace.point("delete oldStateCacheFile", e);
        }

        // == FIX ==
        if (oldScheduleFile.exists()) {
            Log.d("SchemePre36To36", "oldScheduleFile exist");
            fixSchedule();
        }

        if (oldSettingsFile.exists()) {
            Log.d("SchemePre36To36", "oldSettingsFile exist");
            fixSettings();
        }
        // -- FIX --

        try {
            oldSettingsFile.delete();
        } catch (Exception e) {
            appTrace.point("delete oldSettingsFile", e);
        }

        try {
            oldScheduleFile.delete();
        } catch (Exception e) {
            appTrace.point("delete oldScheduleFile", e);
        }

        if (newSchedule != null) {
            FileUtil.write(newScheduleFile, gson.toJson(newSchedule, V37AppSchedule.class));
        }

        if (newSettings != null) {
            FileUtil.write(newSettingsFile, gson.toJson(newSettings, V37Settings.class));
        }

        try {
            FileUtil.deleteDir(context.getExternalCacheDir());
        } catch (Exception e) {
            appTrace.point("datafixer clear cache dir", e);
        }
    }

    private void fixSchedule() {
        Log.d("SchemePre36To36", "fixSchedule();");
        String fileContent;
        try {
            fileContent = FileUtil.read(oldScheduleFile, "{}");
            oldSchedule = gson.fromJson(fileContent, V33Schedule.class);
        } catch (Exception e) {
            appTrace.point("fixSchedule parse exception", e);
            oldSchedule = null;
            return;
        }

        newSchedule = new V37AppSchedule();
        UUID[] oldSchedulesKeys = oldSchedule.schedules.keySet().toArray(new UUID[0]);

        int i = 0;
        while (i < oldSchedulesKeys.length) {
            UUID oldScheduleKey = oldSchedulesKeys[i];
            V33LocalSchedule oldLocalSchedule = oldSchedule.schedules.get(oldScheduleKey);

            V37Preset newPreset = new V37Preset();
            newPreset.name = oldLocalSchedule.name == null ? "Unknown" : oldLocalSchedule.name;
            newPreset.author = null;

            final V33Lesson[][] oldDays = {
                    oldLocalSchedule.sunday == null ? null : oldLocalSchedule.sunday.toArray(new V33Lesson[0]),
                    oldLocalSchedule.monday == null ? null : oldLocalSchedule.monday.toArray(new V33Lesson[0]),
                    oldLocalSchedule.tuesday == null ? null : oldLocalSchedule.tuesday.toArray(new V33Lesson[0]),
                    oldLocalSchedule.wednesday == null ? null : oldLocalSchedule.wednesday.toArray(new V33Lesson[0]),
                    oldLocalSchedule.thursday == null ? null : oldLocalSchedule.thursday.toArray(new V33Lesson[0]),
                    oldLocalSchedule.friday == null ? null : oldLocalSchedule.friday.toArray(new V33Lesson[0]),
                    oldLocalSchedule.saturday == null ? null : oldLocalSchedule.saturday.toArray(new V33Lesson[0])
            };

            int dayCoff = 0;
            for (V33Lesson[] oldDay : oldDays) {
                if (oldDay != null) {
                    for (V33Lesson oldLesson : oldDay) {
                        if (oldLesson != null) {
                            UUID oldLessonInfoPointer = oldLesson.lesson;
                            V33LessonInfo oldLessonInfo = oldSchedule.lessons.get(oldLessonInfoPointer);
                            if (oldLessonInfo != null) {
                                newPreset.eventsInfos.put(oldLessonInfoPointer, new V37EventInfo(oldLessonInfo.name));
                                newPreset.eventsPositions.add(new V37Event(
                                        oldLessonInfoPointer,
                                        dayCoff + oldLesson.start,
                                        dayCoff + (oldLesson.start + oldLesson.duration)
                                ));
                            }
                        }
                    }
                }
                dayCoff = dayCoff + (24*60*60);
            }

            newSchedule.presets.put(oldScheduleKey, newPreset);
            i++;
        }
    }

    private void fixSettings() {
        Log.d("SchemePre36To36", "fixSettings();");
        String fileContent;
        try {
            fileContent = FileUtil.read(oldSettingsFile, "{}");
            oldSettings = gson.fromJson(fileContent, V33Settings.class);
        } catch (Exception e) {
            appTrace.point("fixSettings parse exception", e);
            oldSchedule = null;
            return;
        }

        newSettings = new V37Settings();

        newSettings.isDeveloperFeatures = oldSettings.isDeveloperFeatures;
        newSettings.isBuiltinPresetList = oldSettings.isSyncDeveloperSchedule;

        if (newSchedule != null) {
            newSchedule.selectedPresetUUID = oldSettings.selectedLocalSchedule;
        }
    }
}
