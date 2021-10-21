package ru.fazziclay.schoolguide.data.cache;

import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.schedule.State;

public class StateCache extends BaseData {
    public static final short EARLY_FINISHED_FOR_DAY_NOT_SET = -1;

    public static final short FOREGROUND_NOTIFICATION_STATE_DEFAULT = 1;
    public static final short FOREGROUND_NOTIFICATION_STATE_MAIN_NOTIFY = 2;


    public long cacheCreateTime = 0;
    public State vibratedFor = null;
    public short foregroundNotificationState = 0;
    public short earlyFinishedForDay = EARLY_FINISHED_FOR_DAY_NOT_SET;

    @Override
    public void save(String filePath) {
        cacheCreateTime = System.currentTimeMillis();
        super.save(filePath);
    }
}
