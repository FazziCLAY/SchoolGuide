package ru.fazziclay.schoolguide.util;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;

/**
 * Набор утилит для записи обьектов в файл и чтение их обратно в обекты
 * @apiNote Use GSON
 * **/
public class DataUtil {
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

    public static <T> T load(File file, Class<T> clazz) {
        return load(getGson(), file, clazz);
    }

    public static void save(Gson gson, File file, Object obj) {
        if (obj == null) {
            Log.e("Save", "save object is null!");
            if (SchoolGuideApp.isInstanceAvailable()) {
                SchoolGuideApp app = SchoolGuideApp.get();
                if (app != null) {
                    app.getAppTrace().point("save object is null!", new NullPointerException("Exception by fazziclay!"));
                }
            }
            return;
        }
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
