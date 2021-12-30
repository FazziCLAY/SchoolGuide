package ru.fazziclay.schoolguide.data.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.data.BaseData;

public class Settings extends BaseData {
    boolean isDeveloperFeatures = false;
    boolean isVibration = true;
    UUID selectedLocalSchedule = null;
    List<Integer> versionsHistory = new ArrayList<>();
    boolean isSyncDeveloperSchedule = false;
    NotificationStyle notificationStyle = new NotificationStyle();
    int notifyBeforeTime = 3*60*60;
}
