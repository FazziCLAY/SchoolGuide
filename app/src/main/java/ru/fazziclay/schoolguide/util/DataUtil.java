package ru.fazziclay.schoolguide.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class DataUtil {
    public static <T> T load(File file, Class<T> clazz) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

    public static void save(File file, Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileUtil.write(file, gson.toJson(obj, obj.getClass()));
    }
}
