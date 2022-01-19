package ru.fazziclay.schoolguide.app;

import ru.fazziclay.schoolguide.util.data.BaseData;

public class Settings extends BaseData {
    public boolean developerFeatures = false;
    public boolean isScheduleInformatorEnabled = true;
    public int scheduleNotifyBeforeTime = 2*60*60;

    @Override
    public void reset() {
        developerFeatures = false;
    }
}
