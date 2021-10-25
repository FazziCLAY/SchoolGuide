package ru.fazziclay.schoolguide.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import ru.fazziclay.schoolguide.util.FileUtil;

public abstract class BaseData {
    @SerializedName("version")
    public int formatVersion = -1;

    public boolean isFormatVersionDefault() {
        return (formatVersion == -1);
    }

    /**
     * Save data to file
     * @param filePath save to this file
     * **/
    public void save(String filePath) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        FileUtil.write(filePath, gson.toJson(this, this.getClass()));
    }
}
