package ru.fazziclay.schoolguide.data.settings;

import java.util.UUID;

import ru.fazziclay.schoolguide.data.BaseData;

public class Settings extends BaseData {
    boolean isVibration = true;
    boolean isNotification = true;
    UserNotification userNotification = UserNotification.EXTERNAL;
    AppTheme theme = AppTheme.AUTO;
    UUID selectedLocalSchedule = null;
    DeveloperSettings developerSettings = new DeveloperSettings();
}
