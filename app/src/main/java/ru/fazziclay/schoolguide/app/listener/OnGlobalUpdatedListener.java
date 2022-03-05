package ru.fazziclay.schoolguide.app.listener;

import ru.fazziclay.schoolguide.app.global.GlobalBuiltinPresetList;
import ru.fazziclay.schoolguide.app.global.GlobalCacheKeys;
import ru.fazziclay.schoolguide.app.global.GlobalLatestVersionManifest;
import ru.fazziclay.schoolguide.callback.Callback;
import ru.fazziclay.schoolguide.callback.Status;

/**
 * При обновлении глобальных данных
 * @see ru.fazziclay.schoolguide.app.global.GlobalManager
 * **/
public interface OnGlobalUpdatedListener extends Callback {
    Status run(GlobalCacheKeys globalCacheKeys, GlobalLatestVersionManifest globalLatestVersionManifest, GlobalBuiltinPresetList globalBuiltinPresetList);
}
