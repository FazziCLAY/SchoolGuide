package ru.fazziclay.schoolguide.data.manifest;

import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.schedule.Schedule;

public class Manifest extends BaseData {
    int manifestKey = 0;
    boolean isTechnicalWorks = true;

    AppVersion latestVersion = null;
    Schedule developerSchedule = null;

    public Manifest(int manifestKey, boolean isTechnicalWorks, AppVersion latestVersion) {
        this.manifestKey = manifestKey;
        this.isTechnicalWorks = isTechnicalWorks;
        this.latestVersion = latestVersion;
    }
}
