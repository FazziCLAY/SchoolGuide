package ru.fazziclay.schoolguide.app;

import ru.fazziclay.schoolguide.app.scheduleinformator.android.PresetEditActivity;

public class Settings {
    public boolean isDeveloperFeatures = false;
    public int scheduleNotifyBeforeTime = 2*60*60;
    public boolean isStopForegroundIsNone = true;
    public boolean isFirstMonday = true;
    public PresetEditActivity.ColorScheme presetEditColorScheme = PresetEditActivity.ColorScheme.DEFAULT;

    public boolean isBuiltinPresetList = false;
}
