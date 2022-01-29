package ru.fazziclay.schoolguide.callback;

import ru.fazziclay.schoolguide.app.global.GlobalBuiltinPresetList;
import ru.fazziclay.schoolguide.app.global.GlobalKeys;
import ru.fazziclay.schoolguide.app.global.GlobalVersionManifest;

public interface GlobalUpdateListener extends ICallback {
    Status onGlobalUpdate(Exception exception, GlobalKeys globalKeys, GlobalVersionManifest globalVersionManifest, GlobalBuiltinPresetList globalBuiltinPresetList);
}
