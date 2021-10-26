package ru.fazziclay.schoolguide.data.manifest;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class AppVersion {
    int code = 0;
    String name = "Unknown";
    String pageUrl = "https://github.com/fazziclay/schoolguide/";
    String downloadUrl = "https://github.com/fazziclay/scoolguide/";
    HashMap<String, String> changelog = new HashMap<>();

    public AppVersion(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getChangeLog(String language) {
        if (changelog.containsKey(language)) {
            return changelog.get(language);
        }
        return changelog.get("default");
    }

    @NonNull
    @Override
    public String toString() {
        return "AppVersion{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", pageUrl='" + pageUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", changelog=" + changelog +
                '}';
    }
}
