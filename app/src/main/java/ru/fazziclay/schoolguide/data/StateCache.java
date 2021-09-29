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

    public static final short FOREGROUND_NOTIFICATION_STATE_NOT_SET = -1;
    public static final short FOREGROUND_NOTIFICATION_STATE_DEFAULT = 1;
    public static final short FOREGROUND_NOTIFICATION_STATE_MAIN_NOTIFY = 2;
    public short foregroundNotificationState = -1;

    public static final short EARLY_FINISHED_FOR_DAY_NOT_SET = -1;
    public short earlyFinishedForDay = -1;

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
