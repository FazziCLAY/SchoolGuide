package ru.fazziclay.schoolguide.app.listener;

import ru.fazziclay.schoolguide.callback.Callback;
import ru.fazziclay.schoolguide.callback.Status;

public interface PresetListUpdateSignalListener extends Callback {
    Status onSignal();
}
