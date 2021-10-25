package ru.fazziclay.schoolguide.data.manifest;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class AppVersion {
    int versionCode = 0;
    String versionName = "Unknown";
    String versionPageUrl = "https://github.com/fazziclay/schoolguide/";
    String directDownloadUrl = "https://github.com/fazziclay/scoolguide/";
    HashMap<String, String> changelog = new HashMap<>();

    public AppVersion() {}

    @NonNull
    @Override
    public String toString() {
        return "AppVersion{" +
                "versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", versionPageUrl='" + versionPageUrl + '\'' +
                ", directDownloadUrl='" + directDownloadUrl + '\'' +
                ", changelog=" + changelog +
                '}';
    }
}
