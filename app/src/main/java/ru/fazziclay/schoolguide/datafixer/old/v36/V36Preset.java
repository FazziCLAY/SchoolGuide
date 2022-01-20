package ru.fazziclay.schoolguide.datafixer.old.v36;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class V36Preset {
    public String name;
    public String author;

    public HashMap<UUID, V36EventInfo> eventsInfos = new HashMap<>();
    public List<V36Event> eventsPositions = new ArrayList<>();
}
