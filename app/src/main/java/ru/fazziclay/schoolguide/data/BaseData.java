package ru.fazziclay.schoolguide.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.util.FileUtil;

public class BaseData {
    private transient String filePath;
    private transient Gson gson;

    @SerializedName("version")
    public int formatVersion = -1;

    public void save() {
        save(filePath);
    }

    public void save(String filePath) {
        String serializedData = gson.toJson(this, this.getClass());
        FileUtil.write(filePath, serializedData);
    }

    public static BaseData load(Gson gson, String filePath, Class<? extends BaseData> clazz) {
        String readData = FileUtil.read(filePath, SharedConstrains.JSON_EMPTY_OBJECT);
        BaseData deserialized = gson.fromJson(readData, clazz);
        deserialized.gson = gson;
        deserialized.filePath = filePath;
        return deserialized;
    }
}
