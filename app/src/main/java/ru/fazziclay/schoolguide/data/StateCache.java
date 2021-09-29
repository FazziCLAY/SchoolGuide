package ru.fazziclay.schoolguide.data;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.android.service.ForegroundService;

public class StateCache {
    public static final String STATE_CACHE_FILE = "state_cache.json";

    public static String getStateCacheFilePath(Context context) {
        return context.getExternalCacheDir().getAbsoluteFile() + "/" + STATE_CACHE_FILE;
    }

    public static StateCache getCache() {
        return ForegroundService.getInstance().getStateCache();
    }

    public long cacheCreateTime = 0;
    public boolean isNotifiedLessonStart = false;
    public boolean isNotifiedLessonEnd = false;
    public boolean isNotifiedRestEnding = false;

    public StateCache(boolean isNotifiedLessonStart,
                      boolean isNotifiedLessonEnd,
                      boolean isNotifiedRestEnding) {
        updateTime();
        this.isNotifiedLessonStart = isNotifiedLessonStart;
        this.isNotifiedLessonEnd = isNotifiedLessonEnd;
        this.isNotifiedRestEnding = isNotifiedRestEnding;
    }

    public StateCache() {}

    public void updateTime() {
        cacheCreateTime = System.currentTimeMillis();
    }

    public static void save(Context context) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        FileUtil.write(getStateCacheFilePath(context), gson.toJson(getCache(), StateCache.class));
    }
}
