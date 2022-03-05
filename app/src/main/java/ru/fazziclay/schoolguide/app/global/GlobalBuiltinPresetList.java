package ru.fazziclay.schoolguide.app.global;

import com.google.gson.annotations.SerializedName;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.PresetList;

/**
 * <h1>Глобальный Встроенный список пресетов</h1>
 * <p>Aka Built-in schedule</p>
 * @see GlobalManager
 * **/
public class GlobalBuiltinPresetList extends PresetList implements LocalCacheableData {
    /**
     * <h1>Global key</h1>
     * Name "localCacheKey" since v56
     * @see LocalCacheableData
     * **/
    @SerializedName("localCacheKey")
    private int localCacheKey = 0;

    @Override
    public int getLocalCacheKay() {
        return localCacheKey;
    }
}
