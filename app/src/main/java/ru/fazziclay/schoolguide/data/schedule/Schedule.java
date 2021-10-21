package ru.fazziclay.schoolguide.data.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.schedule.info.LessonInfo;
import ru.fazziclay.schoolguide.data.schedule.info.TeacherInfo;

public class Schedule extends BaseData {
    public HashMap<UUID, TeacherInfo> teachers = new HashMap<>();
    public HashMap<UUID, LessonInfo> lessons   = new HashMap<>();
    public List<Lesson> monday    = new ArrayList<>();
    public List<Lesson> tuesday   = new ArrayList<>();
    public List<Lesson> wednesday = new ArrayList<>();
    public List<Lesson> thursday  = new ArrayList<>();
    public List<Lesson> friday    = new ArrayList<>();
    public List<Lesson> saturday  = new ArrayList<>();
    public List<Lesson> sunday    = new ArrayList<>();
}
