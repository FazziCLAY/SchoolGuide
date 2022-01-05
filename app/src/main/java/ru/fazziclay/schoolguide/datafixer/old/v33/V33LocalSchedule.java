package ru.fazziclay.schoolguide.datafixer.old.v33;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class V33LocalSchedule {
    @SerializedName("name")
    public String name;

    @SerializedName("monday")
    public List<V33Lesson> monday    = new ArrayList<>();
    @SerializedName("tuesday")
    public List<V33Lesson> tuesday   = new ArrayList<>();
    @SerializedName("wednesday")
    public List<V33Lesson> wednesday = new ArrayList<>();
    @SerializedName("thursday")
    public List<V33Lesson> thursday  = new ArrayList<>();
    @SerializedName("friday")
    public List<V33Lesson> friday    = new ArrayList<>();
    @SerializedName("saturday")
    public List<V33Lesson> saturday  = new ArrayList<>();
    @SerializedName("sunday")
    public List<V33Lesson> sunday    = new ArrayList<>();
}
