package ru.fazziclay.schoolguide.app.listener;

import ru.fazziclay.schoolguide.app.global.GlobalBuiltinPresetList;
import ru.fazziclay.schoolguide.callback.Callback;
import ru.fazziclay.schoolguide.callback.Status;

public interface OnUpdatePresetListBuiltinSignalListener extends Callback {
    Status onSignal(GlobalBuiltinPresetList globalBuiltinPresetList, boolean status);
}
