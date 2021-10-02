package ru.fazziclay.schoolguide.data.restore_point;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.data.schedule.Schedule;
import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;

public class RestorePointProvider {
    private static final String RESTORE_POINTS_FOLDER_PATH = "restore_points";
    List<RestorePoint> restorePoints = new ArrayList<>();

    public static String getRestorePointsFolderPath(Context context) {
        return context.getExternalFilesDir("") + "/" + RESTORE_POINTS_FOLDER_PATH;
    }

    public RestorePointProvider(Context context) {
        reload(context);
    }

    public void reload(Context context) {
        restorePoints = new ArrayList<>();
        File[] files = FileUtil.getFilesList(getRestorePointsFolderPath(context));
        Gson gson = new Gson();

        for (File file : files) {
            if (file.isFile()) {
                RestorePoint restorePoint = gson.fromJson(FileUtil.read(file.getAbsolutePath(), "{}"), RestorePoint.class);
                restorePoint.fileName = file.getName();
                if (restorePoint.name == null) return;
                this.restorePoints.add(
                        restorePoint
                );
            }
        }
    }

    public void save(Context context) {
        int i = 0;
        while (i < restorePoints.size()) {
            RestorePoint restorePoint = restorePoints.get(i);
            saveRestorePoint(context, restorePoint);
            i++;
        }
    }

    public void restore(ScheduleProvider scheduleProvider, RestorePoint restorePoint) {
        scheduleProvider.setSchedule(restorePoint.schedule.clone());
    }

    public List<RestorePoint> getRestorePoints() {
        return restorePoints;
    }

    public void create(String name, Schedule schedule) {
        RestorePoint restorePoint = new RestorePoint(name, System.currentTimeMillis(), schedule.clone());
        restorePoint.fileName = nameToFileName(name);
        restorePoints.add(restorePoint);
    }

    public void delete(Context context, RestorePoint restorePoint) {
        restorePoints.remove(restorePoint);
        File file = new File(getRestorePointsFolderPath(context) + "/" + restorePoint.fileName);
        file.delete();
    }

    public void saveRestorePoint(Context context, RestorePoint restorePoint) {
        if (restorePoint.name == null) return;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileUtil.write(getRestorePointsFolderPath(context) + "/" + restorePoint.fileName, gson.toJson(restorePoint, RestorePoint.class));
    }

    public String nameToFileName(String name) {
        if (name == null) return null;
        return name.replace(" ", "_").replace("/", "-").replace("\\", "-").replaceAll("\\W+", "_") + ".json";
    }
}
