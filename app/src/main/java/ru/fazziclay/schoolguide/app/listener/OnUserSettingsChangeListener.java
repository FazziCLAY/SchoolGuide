package ru.fazziclay.schoolguide.app.listener;

import ru.fazziclay.schoolguide.callback.Callback;
import ru.fazziclay.schoolguide.callback.Status;

public interface OnUserSettingsChangeListener extends Callback {
    Status run( /* SettingsActivity constants */ String preferenceKey);
}
