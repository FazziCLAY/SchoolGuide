package ru.fazziclay.schoolguide.app;

import ru.fazziclay.schoolguide.app.global.GlobalBuiltinPresetList;
import ru.fazziclay.schoolguide.app.global.GlobalKeys;
import ru.fazziclay.schoolguide.app.global.GlobalVersionManifest;
import ru.fazziclay.schoolguide.callback.Callback;
import ru.fazziclay.schoolguide.callback.Status;

public interface GlobalUpdateListener extends Callback {
    Status onGlobalUpdate(GlobalKeys globalKeys, GlobalVersionManifest globalVersionManifest, GlobalBuiltinPresetList globalBuiltinPresetList);
}
