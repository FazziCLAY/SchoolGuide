package ru.fazziclay.schoolguide.util;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;

import ru.fazziclay.schoolguide.app.MilkLog;
import ru.fazziclay.schoolguide.app.SchoolGuideApp;

/**
 * Набор утилит для записи обьектов в файл и чтение их обратно в обекты
 * @apiNote Use GSON
 * **/
public class DataUtil {
    @NonNull
    public static <T> T load(Gson gson, File file, Class<T> clazz) {
        T data = gson.fromJson("{}", clazz);

        if (file.exists()) {
            String fileContent = FileUtil.read(file);
            try {
                T temp = gson.fromJson(fileContent, clazz);
                if (temp != null) {
                    data = temp;
                }
            } catch (Exception ignored) {}
        }

        return data;
    }

    @NonNull
    public static <T> T load(File file, Class<T> clazz) {
        return load(getGson(), file, clazz);
    }

    public static void save(Gson gson, File file, Object obj) {
        if (obj == null) {
            MilkLog.g("object null", new NullPointerException("save object is null!"));
            return;
        }
        FileUtil.write(file, gson.toJson(obj, obj.getClass()));
    }

    public static void save(File file, Object obj) {
        save(getGson(), file, obj);
    }

    private static Gson getGson() {
        if (SchoolGuideApp.isInstanceAvailable()) {
            SchoolGuideApp app = SchoolGuideApp.get();
            if (app != null) return app.getGson();
        }
        MilkLog.g("DataUtil.getGson: SchoolGuide instance not available!");
        return new Gson();
    }
}
