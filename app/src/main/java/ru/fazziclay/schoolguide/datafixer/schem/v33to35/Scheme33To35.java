package ru.fazziclay.schoolguide.datafixer.schem.v33to35;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.UUID;

import ru.fazziclay.schoolguide.app.scheduleinformator.AppSchedule;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Event;
import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.Preset;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33Lesson;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33LessonInfo;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33LocalSchedule;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33Schedule;
import ru.fazziclay.schoolguide.datafixer.old.v33.V33Settings;
import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.util.DataUtil;
import ru.fazziclay.schoolguide.util.FileUtil;

public class Scheme33To35 extends AbstractScheme {
    private static final String OLD_SCHEDULE_FILE = "schedule.json";
    private static final String OLD_SETTINGS_FILE = "settings.json";

    private static final String NEW_SCHEDULE_FILE = "scheduleinformator.app_schedule.json";

    Gson gson;
    String externalFilesPath;
    String externalCachePath;

    AppSchedule newSchedule;

    File oldScheduleFile;
    V33Schedule oldSchedule;

    File oldSettingsFile;
    V33Settings oldSettings;

    Preset currentPreset;
    V33LocalSchedule currentLocalSchedule;


    @Override
    public boolean isCompatible(int version) {
        return version <= 33;
    }

    @Override
    public int run(Context context, int version) {
        gson = new Gson();
        externalFilesPath = context.getExternalFilesDir(null).getAbsolutePath() + "/";
        externalCachePath = context.getExternalCacheDir().getAbsolutePath() + "/";
        newSchedule = new AppSchedule();

        try {
            oldScheduleFile = new File(externalFilesPath + OLD_SCHEDULE_FILE);
            if (oldScheduleFile.exists()) {
                oldSchedule = gson.fromJson(FileUtil.read(oldScheduleFile.getAbsolutePath(), "json crash"), V33Schedule.class);
                fixSchedule();

                oldScheduleFile.delete();
            }

            oldSettingsFile = new File(externalFilesPath + OLD_SETTINGS_FILE);
            if (oldSettingsFile.exists()) {
                oldSettings = gson.fromJson(FileUtil.read(oldSettingsFile.getAbsolutePath(), "json crash"), V33Settings.class);

                newSchedule.setSelectedPreset(newSchedule.getPreset(oldSettings.selectedLocalSchedule));

                oldSettingsFile.delete();
            }

            DataUtil.save(new File(context.getExternalCacheDir().getAbsolutePath(), NEW_SCHEDULE_FILE), newSchedule);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            deleteNonUsed(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 35;
    }

    public void deleteNonUsed(Context context) {
        File settings = new File(externalFilesPath + OLD_SETTINGS_FILE);
        settings.delete();

        File cache = context.getExternalCacheDir();
        File[] list = cache.listFiles();
        if (list != null) {
            for (File file : list) {
                file.delete();
            }
        }
        cache.delete();
    }

    public void fixSchedule() {
        for (UUID localScheduleKey : oldSchedule.schedules.keySet()) {
            currentLocalSchedule = oldSchedule.schedules.get(localScheduleKey);
            if (currentLocalSchedule == null) continue;

            currentPreset = localScheduleToPreset(currentLocalSchedule);
            currentPreset.name = currentLocalSchedule.name;
            newSchedule.putPreset(localScheduleKey, currentPreset);
        }
    }

    public Preset localScheduleToPreset(V33LocalSchedule localSchedule) {
        Preset preset = new Preset();

        final V33Lesson[][] days = {
                localSchedule.sunday == null ? null : localSchedule.sunday.toArray(new V33Lesson[0]),
                localSchedule.monday == null ? null : localSchedule.monday.toArray(new V33Lesson[0]),
                localSchedule.tuesday == null ? null : localSchedule.tuesday.toArray(new V33Lesson[0]),
                localSchedule.wednesday == null ? null : localSchedule.wednesday.toArray(new V33Lesson[0]),
                localSchedule.thursday == null ? null : localSchedule.thursday.toArray(new V33Lesson[0]),
                localSchedule.friday == null ? null : localSchedule.friday.toArray(new V33Lesson[0]),
                localSchedule.saturday == null ? null : localSchedule.saturday.toArray(new V33Lesson[0])
        };

        int dayCoff = 0;
        for (V33Lesson[] day : days) {
            if (day == null) continue;
            for (V33Lesson lesson : day) {
                if (lesson == null) continue;
                UUID uuid = lesson.lesson;
                V33LessonInfo lessonInfo = oldSchedule.lessons.get(uuid);
                if (lessonInfo != null) preset.eventInfos.put(uuid, lessonInfo.toEventInfo());
                preset.events.add(new Event(uuid, dayCoff + lesson.start, dayCoff + (lesson.start + lesson.duration)));
            }
            dayCoff = dayCoff + (24*60*60);
        }

        return preset;
    }
}
