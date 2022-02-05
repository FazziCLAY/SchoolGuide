package ru.fazziclay.schoolguide.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.UUID;

public class UUIDUtilTest {
    @Test
    public void exclusions() {
        UUID exclusion = new UUID(0, 0);
        UUID[] exclusions = {
                exclusion
        };

        int i = 0;
        final int MAX = 100000;
        while (i < MAX) {
            UUID uuid = UUIDUtil.generateUUID(exclusions);
            assertNotEquals(exclusion, uuid);
            i++;
        }
    }
}
