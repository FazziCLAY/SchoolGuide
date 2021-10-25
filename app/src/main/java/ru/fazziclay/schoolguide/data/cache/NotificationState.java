package ru.fazziclay.schoolguide.data.cache;

import com.google.gson.annotations.SerializedName;

public enum NotificationState {
    @SerializedName("default")
    DEFAULT,
    @SerializedName("custom")
    CUSTOM
}
