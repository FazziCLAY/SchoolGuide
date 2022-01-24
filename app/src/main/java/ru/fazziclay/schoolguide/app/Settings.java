package ru.fazziclay.schoolguide.app;

import ru.fazziclay.schoolguide.app.scheduleinformator.android.PresetEditActivity;

public class Settings {
    public boolean developerFeatures = false;
    public int scheduleNotifyBeforeTime = 2*60*60;
    public final boolean stopForegroundIsNone = false;
    public boolean isFirstMonday = true;
    public PresetEditActivity.ColorScheme presetEditColorScheme = PresetEditActivity.ColorScheme.DEFAULT;
}
