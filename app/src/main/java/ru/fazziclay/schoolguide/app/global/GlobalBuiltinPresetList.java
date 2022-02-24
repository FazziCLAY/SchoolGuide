package ru.fazziclay.schoolguide.app.global;

import com.google.gson.annotations.SerializedName;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.PresetList;

/**
 * <h1>Глобальный Встроенный список пресетов</h1>
 * <p>Aka Built-in schedule</p>
 * @see GlobalManager
 * **/
public class GlobalBuiltinPresetList extends PresetList implements IGlobalData {
    /**
     * <h1>Global key</h1>
     * Name "key" since pre v50
     * @see IGlobalData
     * **/
    @SerializedName("key")
    private final int key = 0;

    @Override
    public int getGlobalKey() {
        return key;
    }
}
