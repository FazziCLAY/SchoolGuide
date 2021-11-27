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
        save();
    }

    @Override
    public BaseData load() {
        Gson gson = new Gson();
        return gson.fromJson(FileUtil.read(filePath, "{}"), StateCache.class);
    }

    public StateCache getStateCache() {
        return (StateCache) data;
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

    public long getLatestAutoManifestCheck() {
        return getStateCache().latestAutoManifestCheck;
    }

    public void setLatestAutoManifestCheck() {
        getStateCache().latestAutoManifestCheck = System.currentTimeMillis() / 1000;
        save();
    }
}
