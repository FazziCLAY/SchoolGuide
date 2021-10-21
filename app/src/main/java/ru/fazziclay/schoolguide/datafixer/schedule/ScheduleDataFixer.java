package ru.fazziclay.schoolguide.datafixer.schedule;

import com.google.gson.Gson;
import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.datafixer.schedule.v0.OldV0Schedule;

public class ScheduleDataFixer extends DataFixer {
    public String json = "{}";
    Gson gson = new Gson();

    public String fix(final String sourceJson) {
        this.json = sourceJson;

        ScheduleBaseObject scheduleBaseObject = gson.fromJson(this.json, ScheduleBaseObject.class);
        if (scheduleBaseObject.formatVersion == 0) {
            fixV0();
        }

        return this.json;
    }

    private void fixV0() {
        OldV0Schedule oldV0Schedule = gson.fromJson(json, OldV0Schedule.class);
    }
}
