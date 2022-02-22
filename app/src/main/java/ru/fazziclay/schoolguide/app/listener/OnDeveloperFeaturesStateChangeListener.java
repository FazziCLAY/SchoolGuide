package ru.fazziclay.schoolguide.app.listener;

import ru.fazziclay.schoolguide.callback.Callback;
import ru.fazziclay.schoolguide.callback.Status;

public interface OnDeveloperFeaturesStateChangeListener extends Callback {
    Status onDeveloperFeaturesChange(boolean enabled);
}
