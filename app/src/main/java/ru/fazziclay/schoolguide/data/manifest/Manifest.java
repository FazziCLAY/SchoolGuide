package ru.fazziclay.schoolguide.data.manifest;

import androidx.annotation.NonNull;

import ru.fazziclay.schoolguide.data.BaseData;

public class Manifest extends BaseData {
    int manifestKey = 0;
    AppVersion latestVersion = null;
    AppVersion appVersion = null;

    @NonNull
    @Override
    public String toString() {
        return "Manifest{" +
                "formatVersion=" + formatVersion +
                ", manifestKey=" + manifestKey +
                ", latestVersion=" + latestVersion +
                '}';
    }
}
