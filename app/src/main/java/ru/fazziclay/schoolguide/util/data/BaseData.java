package ru.fazziclay.schoolguide.util.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import ru.fazziclay.schoolguide.util.FileUtil;

public class BaseData {
    public static BaseData load(File file, Class<? extends BaseData> clazz) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BaseData data = gson.fromJson("{}", clazz);
        data.reset();

        if (file.exists()) {
            String fileContent = FileUtil.read(file);
            try {
                data = gson.fromJson(fileContent, clazz);
            } catch (Exception ignored) {
            }
        }

        data.gson = gson;
        data.filePath = file.getAbsolutePath();
        return data;
    }

    public transient Gson gson;
    public transient String filePath;

    public BaseData() {}

    public BaseData(Gson gson, String filePath) {
        this.gson = gson;
        this.filePath = filePath;
    }

    public void save() {
        FileUtil.write(filePath, gson.toJson(this, this.getClass()));
    }

    public void reset() {}
}
