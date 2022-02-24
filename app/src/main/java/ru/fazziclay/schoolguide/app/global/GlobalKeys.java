package ru.fazziclay.schoolguide.app.global;

import com.google.gson.annotations.SerializedName;

/**
 * Хранилеще глобальных ключей
 * @see GlobalManager
 * **/
public class GlobalKeys {
    /**
     * <h1>Ключ манифеста версий({@link GlobalVersionManifest})</h1>
     * Name "versionManifest" since pre v50
     * **/
    @SerializedName("versionManifest")
    private final int versionManifest = 0;

    /**
     * <h1>Ключ встроенного списка пресетов({@link GlobalBuiltinPresetList})</h1>
     * Name "builtinSchedule" since pre v50
     * **/
    @SerializedName("builtinSchedule")
    private final int builtinSchedule = 0;

    /**
     * @see GlobalKeys#versionManifest
     * **/
    public int getVersionManifest() {
        return versionManifest;
    }

    /**
     * @see GlobalKeys#builtinSchedule
     * **/
    public int getBuiltinSchedule() {
        return builtinSchedule;
    }
}
