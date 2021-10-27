package ru.fazziclay.schoolguide.data.manifest;

import ru.fazziclay.schoolguide.data.BaseData;

public class Manifest extends BaseData {
    int manifestKey = 0;
    boolean isTechnicalWorks = true;
    AppVersion latestVersion = null;

    public Manifest(int manifestKey, boolean isTechnicalWorks, AppVersion latestVersion) {
        this.manifestKey = manifestKey;
        this.isTechnicalWorks = isTechnicalWorks;
        this.latestVersion = latestVersion;
    }
}
