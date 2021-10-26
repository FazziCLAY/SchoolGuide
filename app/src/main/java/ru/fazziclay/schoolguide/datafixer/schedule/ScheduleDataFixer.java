package ru.fazziclay.schoolguide.datafixer.schedule;

import com.google.gson.Gson;

import ru.fazziclay.schoolguide.data.schedule.ScheduleProvider;
import ru.fazziclay.schoolguide.datafixer.DataFixer;

public class ScheduleDataFixer extends DataFixer {
    public String json = "{}";
    Gson gson = new Gson();

    public String fix(final String sourceJson) {
        this.json = sourceJson;

        ScheduleBaseObject scheduleBaseObject = gson.fromJson(this.json, ScheduleBaseObject.class);
        if (scheduleBaseObject.formatVersion == 0) {
            fixV0();
        }

        int i = 0;
        int maxI = 40;

        int formatVersion = scheduleBaseObject.formatVersion;
        while (formatVersion < ScheduleProvider.CURRENT_FORMAT_VERSION) {
            // first published version
            if (formatVersion <= 0) formatVersion = fixV0();

            // fix versions space (1 - 3 not)
            if (formatVersion > 0 && formatVersion < 4) formatVersion = 4;

            // =========================
            if (i > maxI) {
                break;
            }
            i++;
        }

        return this.json;
    }

    private int fixV0() {
        return 4;
    }
}
