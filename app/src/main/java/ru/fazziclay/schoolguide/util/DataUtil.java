package ru.fazziclay.schoolguide.util;

import com.google.gson.Gson;

import java.io.File;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class DataUtil {
    public static <T> T load(Gson gson, File file, Class<T> clazz) {
        T data = gson.fromJson("{}", clazz);

        if (file.exists()) {
            String fileContent = FileUtil.read(file);
            try {
                data = gson.fromJson(fileContent, clazz);
            } catch (Exception ignored) {
            }
        }

        return data;
    }

    public static <T> T load(File file, Class<T> clazz) {
        return load(getGson(), file, clazz);
    }

    public static void save(Gson gson, File file, Object obj) {
        FileUtil.write(file, gson.toJson(obj, obj.getClass()));
    }

    public static void save(File file, Object obj) {
        save(getGson(), file, obj);
    }

    private static Gson getGson() {
        if (SchoolGuideApp.isInstanceAvailable()) {
            return SchoolGuideApp.get().getGson();
        }
        return new Gson();
    }
}
