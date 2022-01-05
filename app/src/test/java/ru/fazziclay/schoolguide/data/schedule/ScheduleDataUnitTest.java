package ru.fazziclay.schoolguide.data.schedule;

import org.junit.Test;
import static org.junit.Assert.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.UUID;

import ru.fazziclay.schoolguide.util.time.TimeUtil;

public class ScheduleDataUnitTest {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void checkSharedConstrains() {
        assertEquals(604800, TimeUtil.SECONDS_IN_WEEK);
    }

    @Test
    public void saveNoCreatedFile() {
        UUID n = UUID.nameUUIDFromBytes(new byte[]{0, 0, 0, 0});
        UUID n2 = UUID.nameUUIDFromBytes(new byte[]{0, 13, 11, 0});
    }
}