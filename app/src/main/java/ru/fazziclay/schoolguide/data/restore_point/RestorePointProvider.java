package ru.fazziclay.schoolguide.data.restore_point;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.data.BaseProvider;
import ru.fazziclay.schoolguide.data.schedule.Schedule;

public class RestorePointProvider extends BaseProvider {
    private static final String RESTORE_POINTS_FOLDER_PATH = "restore_points";
    String folderPath;
    List<RestorePoint> restorePoints;

    public RestorePointProvider(Context context) {
        folderPath = context.getExternalFilesDir(null).getAbsolutePath().concat("/").concat(RESTORE_POINTS_FOLDER_PATH);
        restorePoints = loadAll();
    }

    public void create(String name, Schedule schedule) {
        RestorePoint restorePoint = new RestorePoint(name, System.currentTimeMillis(), schedule);
        restorePoint.fileName = fixName(name);
        restorePoints.add(restorePoint);
        save();
    }

    public void delete(RestorePoint restorePoint) {
        restorePoints.remove(restorePoint);
        File file = new File(folderPath + "/" + restorePoint.fileName);
        file.delete();
        save();
    }

    public void rename(RestorePoint restorePoint, String name) {
        restorePoint.name = name;
        save();
    }

    public String fixName(String name) {
        return name.replace(" ", "_").replace("/", "-").replace("\\", "-").replaceAll("\\W+", "_") + ".json";
    }

    public List<RestorePoint> getRestorePoints() {
        return restorePoints;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public List<RestorePoint> loadAll() {
        List<RestorePoint> restorePoints = new ArrayList<>();
        File[] folderFiles = FileUtil.getFilesList(folderPath);

        for (File file : folderFiles) {
            if (!file.isFile()) continue;

            RestorePoint restorePoint = load(file.getName());
            if (restorePoint.name == null) continue;
            restorePoints.add(restorePoint);
        }

        return restorePoints;
    }

    public void reload() {
        restorePoints = loadAll();
    }

    public RestorePoint load(String fileName) {
        Gson gson = new Gson();
        RestorePoint restorePoint = gson.fromJson(FileUtil.read(folderPath + "/" + fileName, "{}"), RestorePoint.class);
        restorePoint.fileName = fileName;
        return restorePoint;
    }

    @Override
    public void save() {
        int i = 0;
        while (i < restorePoints.size()) {
            RestorePoint restorePoint = restorePoints.get(i);
            restorePoint.save(folderPath + "/" + restorePoint.fileName);
            i++;
        }
    }
}
