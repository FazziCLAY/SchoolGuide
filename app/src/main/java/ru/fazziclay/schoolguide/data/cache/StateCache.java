package ru.fazziclay.schoolguide.data.cache;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.schedule.State;

public class StateCache extends BaseData {
    long cacheCreateTime = 0;
    int latestAppVersionUseCode = SharedConstrains.APPLICATION_VERSION_CODE;
    State vibratedFor = State.END;
    NotificationState foregroundNotificationState = NotificationState.DEFAULT;
    NotificationState externalNotificationState = NotificationState.DEFAULT;

    @Override
    public void save(String filePath) {
        cacheCreateTime = System.currentTimeMillis();
        super.save(filePath);
    }
}
