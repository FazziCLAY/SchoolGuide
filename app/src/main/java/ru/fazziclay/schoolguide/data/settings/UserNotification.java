package ru.fazziclay.schoolguide.data.settings;

import com.google.gson.annotations.SerializedName;

public enum UserNotification {
    @SerializedName("foreground")
    FOREGROUND,
    @SerializedName("external")
    EXTERNAL,
    @SerializedName("disabled")
    DISABLED
}
