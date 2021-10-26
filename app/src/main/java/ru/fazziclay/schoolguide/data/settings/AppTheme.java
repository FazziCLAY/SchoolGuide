package ru.fazziclay.schoolguide.data.settings;

import com.google.gson.annotations.SerializedName;

public enum AppTheme {
    @SerializedName("light")
    LIGHT,
    @SerializedName("night")
    NIGHT,
    @SerializedName("auto")
    AUTO
}
