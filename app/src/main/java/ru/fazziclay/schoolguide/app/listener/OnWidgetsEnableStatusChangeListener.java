package ru.fazziclay.schoolguide.app.listener;

import ru.fazziclay.schoolguide.callback.Callback;
import ru.fazziclay.schoolguide.callback.Status;

/**
 * При изменении статуса виджета
 * @see ru.fazziclay.schoolguide.app.MainWidget
 * **/
public interface OnWidgetsEnableStatusChangeListener extends Callback {
    Status onChange(boolean status);
}
