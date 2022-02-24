package ru.fazziclay.schoolguide.app.listener;

import ru.fazziclay.schoolguide.app.global.GlobalBuiltinPresetList;
import ru.fazziclay.schoolguide.app.global.GlobalKeys;
import ru.fazziclay.schoolguide.app.global.GlobalVersionManifest;
import ru.fazziclay.schoolguide.callback.Callback;
import ru.fazziclay.schoolguide.callback.Status;

public interface OnGlobalUpdatedListener extends Callback {
    Status run(GlobalKeys globalKeys, GlobalVersionManifest globalVersionManifest, GlobalBuiltinPresetList globalBuiltinPresetList);
}
