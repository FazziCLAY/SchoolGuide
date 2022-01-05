package ru.fazziclay.schoolguide.app.scheduleinformator.appschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class Preset {
    public String name;
    public String author;

    public HashMap<UUID, EventInfo> eventInfos = new HashMap<>();
    public List<Event> events = new ArrayList<>();

    public Event getNowEvent() {
        int i = 0;
        while (i < events.size()) {
            Event event = events.get(i);
            if (event.isNow()) return event;
            i++;
        }
        return null;
    }

    public Event getNextEvent() {
        Event event = null;
        int m = Integer.MAX_VALUE;

        int i = 0;
        while (i < events.size()) {
            Event event1 = events.get(i);
            if (TimeUtil.getWeekSeconds() < event1.start && event1.start < m) {
                event = event1;
                m = event1.start;
            }
            i++;
        }
        return event;
    }

    public CompressedEvent getNowCompressedEvent() {
        return eventCompress(getNowEvent());
    }

    public CompressedEvent getNextCompressedEvent() {
        return eventCompress(getNextEvent());
    }

    private CompressedEvent eventCompress(Event event) {
        if (event == null) return null;
        EventInfo eventInfo = getEventInfo(event.externalEvent);
        String name = "UNKNOWN";
        if (eventInfo != null) {
            name = eventInfo.name;
        }
        return new CompressedEvent(name, event.start, event.end);
    }

    public EventInfo getEventInfo(UUID uuid) {
        return eventInfos.get(uuid);
    }
}
