package ru.fazziclay.schoolguide.datafixer.old.v33;

import com.google.gson.annotations.SerializedName;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.EventInfo;

public class V33LessonInfo {
    @SerializedName("name")
    public String name;

    public EventInfo toEventInfo() {
        return new EventInfo(name);
    }
}
