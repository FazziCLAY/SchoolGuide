package ru.fazziclay.schoolguide.datafixer.old.v37;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class V37Preset {
    public String name;
    public String author;

    public HashMap<UUID, V37EventInfo> eventsInfos = new HashMap<>();
    public List<V37Event> eventsPositions = new ArrayList<>();
}
