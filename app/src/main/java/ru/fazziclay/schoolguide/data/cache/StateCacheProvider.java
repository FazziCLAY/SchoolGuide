package ru.fazziclay.schoolguide.data.cache;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.BaseProvider;
import ru.fazziclay.schoolguide.data.schedule.State;
import ru.fazziclay.schoolguide.data.settings.Settings;

public class StateCacheProvider extends BaseProvider {
    private static final String STATE_CACHE_FILE = "state_cache.json";

    public StateCacheProvider(Context context) {
        filePath = context.getExternalCacheDir().getAbsolutePath().concat("/").concat(STATE_CACHE_FILE);
        data = load();
    }

    public void setEarlyFinishedForDay(short forDay) {
        ((StateCache) data).earlyFinishedForDay = forDay;
        save();
    }

    public short getEarlyFinishedDay() {
        return ((StateCache) data).earlyFinishedForDay;
    }

    public void earlyFinishForToday() {
        setEarlyFinishedForDay((short) new GregorianCalendar().get(Calendar.DAY_OF_YEAR));
    }

    public void cancelEarlyFinish() {
        setEarlyFinishedForDay(StateCache.EARLY_FINISHED_FOR_DAY_NOT_SET);
    }

    public boolean isEarlyFinishedForToday() {
        return (getEarlyFinishedDay() == (short) new GregorianCalendar().get(Calendar.DAY_OF_YEAR));
    }

    public void setForegroundNotificationState(short s) {
        ((StateCache) data).foregroundNotificationState = s;
        save();
    }

    public boolean isForegroundNotificationStateNotDefault() {
        return ((StateCache) data).foregroundNotificationState != StateCache.FOREGROUND_NOTIFICATION_STATE_DEFAULT;
    }

    public State getVibratedFor() {
        return ((StateCache) data).vibratedFor;
    }

    public void setVibratedFor(State state) {
        ((StateCache) data).vibratedFor = state;
        save();
    }


    @Override
    public BaseData load() {
        Gson gson = new Gson();
        return gson.fromJson(FileUtil.read(filePath, "{}"), StateCache.class);
    }
}
