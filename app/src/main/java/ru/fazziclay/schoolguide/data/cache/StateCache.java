package ru.fazziclay.schoolguide.data.cache;

import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.schedule.State;

public class StateCache extends BaseData {
    long cacheCreateTime = 0;
    State vibratedFor = State.END;
    NotificationState foregroundNotificationState = NotificationState.DEFAULT;
    NotificationState externalNotificationState = NotificationState.DEFAULT;
    int lessonsActivityLastClickedTo = 0;
    long lessonsActivityLastClickedTime = 0;

    @Override
    public void save(String filePath) {
        cacheCreateTime = System.currentTimeMillis();
        super.save(filePath);
    }
}
