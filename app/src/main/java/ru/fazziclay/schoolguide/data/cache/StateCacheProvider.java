package ru.fazziclay.schoolguide.data.cache;

import android.content.Context;

import com.google.gson.Gson;

import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.BaseProvider;
import ru.fazziclay.schoolguide.data.schedule.State;
import ru.fazziclay.schoolguide.util.FileUtil;

public class StateCacheProvider extends BaseProvider {
    private static final String STATE_CACHE_FILE = "state_cache.json";

    public StateCacheProvider(Context context) {
        filePath = context.getExternalCacheDir().getAbsolutePath().concat("/").concat(STATE_CACHE_FILE);
        data = load();
        if (data.isFormatVersionDefault()) data.formatVersion = 1;
    }

    public int getLatestAppVersionUseCode() {
        return getStateCache().latestAppVersionUseCode;
    }

    public StateCache getStateCache() {
        return (StateCache) data;
    }

    public void setForegroundNotificationState(NotificationState s) {
        if (getStateCache().foregroundNotificationState != s) {
            getStateCache().foregroundNotificationState = s;
            save();
        }
    }

    public boolean isForegroundNotificationStateDefault() {
        return getStateCache().foregroundNotificationState == NotificationState.DEFAULT;
    }

    public void setExternalNotificationState(NotificationState s) {
        if (getStateCache().externalNotificationState != s) {
            getStateCache().externalNotificationState = s;
            save();
        }
    }

    public boolean isExternalNotificationStateDefault() {
        return getStateCache().externalNotificationState == NotificationState.DEFAULT;
    }

    public State getVibratedFor() {
        return getStateCache().vibratedFor;
    }

    public void setVibratedFor(State state) {
        if (getStateCache().vibratedFor != state) {
            getStateCache().vibratedFor = state;
            save();
        }
    }

    @Override
    public BaseData load() {
        Gson gson = new Gson();
        return gson.fromJson(FileUtil.read(filePath, "{}"), StateCache.class);
    }
}
