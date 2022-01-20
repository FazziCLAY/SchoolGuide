package ru.fazziclay.schoolguide.datafixer.schem.v33to35;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.UUID;

import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.datafixer.Version;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33Lesson;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33LessonInfo;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33LocalSchedule;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33Schedule;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33Settings;
import ru.fazziclay.schoolguide.datafixer.old.v36.V36AppSchedule;
import ru.fazziclay.schoolguide.datafixer.old.v36.V36Event;
import ru.fazziclay.schoolguide.datafixer.old.v36.V36EventInfo;
import ru.fazziclay.schoolguide.datafixer.old.v36.V36Preset;
import ru.fazziclay.schoolguide.datafixer.old.v36.V36Settings;
import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.util.FileUtil;

public class SchemePre36To36 extends AbstractScheme {
    final String OLD_MANIFEST_FILE = "manifest.json";
    final String OLD_STATECACHE_FILE = "state_cache.json";
    final String OLD_SCHEDULE_FILE = "schedule.json";
    final String OLD_SETTINGS_FILE = "settings.json";

    final String NEW_SCHEDULE_FILE = "scheduleinformator.schedule.json";
    final String NEW_SETTINGS_FILE = "settings.json";

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
    V36AppSchedule newSchedule;

    V33Settings oldSettings;
    V36Settings newSettings;

    @Override
    public boolean isCompatible(Version version) {
        Log.d("SchemePre36To36", "isCompatible = "+version.getLatestVersion());
        return version.getLatestVersion() < 36;
    }

    @Override
    public Version run(DataFixer dataFixer, Version version) {
        Log.d("SchemePre36To36", "run");
        gson = new GsonBuilder().setPrettyPrinting().create();
        context = dataFixer.getAndroidContext();
        cacheDir = context.getExternalCacheDir();
        filesDir = context.getExternalFilesDir(null);

        oldManifestFile = new File(cacheDir, OLD_MANIFEST_FILE);
        oldStateCacheFile = new File(cacheDir, OLD_STATECACHE_FILE);
        oldScheduleFile = new File(filesDir, OLD_SCHEDULE_FILE);
        oldSettingsFile = new File(filesDir, OLD_SETTINGS_FILE);

        newScheduleFile = new File(filesDir, NEW_SCHEDULE_FILE);
        newSettingsFile = new File(filesDir, NEW_SETTINGS_FILE);

        fix();

        version.setLatestVersion(36);
        return version;
    }

    private void fix() {
        Log.d("SchemePre36To36", "fix");
        try {
            if (oldManifestFile.exists()) oldManifestFile.delete();
        } catch (Exception ignored) {}

        try {
            if (oldStateCacheFile.exists()) oldStateCacheFile.delete();
        } catch (Exception ignored) {}

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
        } catch (Exception ignored) {}

        try {
            oldScheduleFile.delete();
        } catch (Exception ignored) {}

        if (newSchedule != null) {
            FileUtil.write(newScheduleFile, gson.toJson(newSchedule, V36AppSchedule.class));
        }

        if (newSettings != null) {
            FileUtil.write(newSettingsFile, gson.toJson(newSettings, V36Settings.class));
        }
    }

    private void fixSchedule() {
        Log.d("SchemePre36To36", "fixSchedule();");
        String fileContent;
        try {
            fileContent = FileUtil.read(oldScheduleFile, "{}");
            oldSchedule = gson.fromJson(fileContent, V33Schedule.class);
        } catch (Exception ignored) {
            oldSchedule = null;
            return;
        }

        newSchedule = new V36AppSchedule();
        UUID[] oldSchedulesKeys = oldSchedule.schedules.keySet().toArray(new UUID[0]);

        int i = 0;
        while (i < oldSchedulesKeys.length) {
            UUID oldScheduleKey = oldSchedulesKeys[i];
            V33LocalSchedule oldLocalSchedule = oldSchedule.schedules.get(oldScheduleKey);

            V36Preset newPreset = new V36Preset();
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
                                newPreset.eventsInfos.put(oldLessonInfoPointer, new V36EventInfo(oldLessonInfo.name));
                                newPreset.eventsPositions.add(new V36Event(
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
        } catch (Exception ignored) {
            oldSchedule = null;
            return;
        }

        newSettings = new V36Settings();

        newSettings.developerFeatures = oldSettings.isDeveloperFeatures;
        if (newSchedule != null) {
            newSchedule.currentPresetUUID = oldSettings.selectedLocalSchedule;
        }
    }
}
