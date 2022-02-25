package ru.fazziclay.schoolguide.app.listener;

import ru.fazziclay.schoolguide.callback.Callback;
import ru.fazziclay.schoolguide.callback.Status;

/**
 * Сигнал для обновления списоков с PresetList в интерфейсе
 * **/
public interface PresetListUpdateSignalListener extends Callback {
    Status onSignal();
}
