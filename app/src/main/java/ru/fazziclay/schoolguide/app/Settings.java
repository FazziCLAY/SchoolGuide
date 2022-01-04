package ru.fazziclay.schoolguide.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.fazziclay.schoolguide.util.FileUtil;

public class Settings {
    public static final String FILE = "schoolguide.settings.json";

    public String filePath;
    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileUtil.write(filePath, gson.toJson(this, this.getClass()));
    }

    public boolean a;
}
