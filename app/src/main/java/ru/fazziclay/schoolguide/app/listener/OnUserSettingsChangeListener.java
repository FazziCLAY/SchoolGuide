package ru.fazziclay.schoolguide.app.listener;

import ru.fazziclay.schoolguide.callback.Callback;
import ru.fazziclay.schoolguide.callback.Status;

/**
 * Пользователь поменял настрйку
 * @see ru.fazziclay.schoolguide.app.Settings
 * @see ru.fazziclay.schoolguide.app.SettingsActivity
 * **/
public interface OnUserSettingsChangeListener extends Callback {
    /**
     * @param preferenceKey string key from SettingsActivity constants
     * **/
    Status run(String preferenceKey);
}
