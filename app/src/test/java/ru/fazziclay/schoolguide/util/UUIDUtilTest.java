package ru.fazziclay.schoolguide.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.UUID;

public class UUIDUtilTest {
    @Test
    public void test() {
        UUID exclusion = new UUID(0, 0);
        UUID[] exclusions = {
                exclusion
        };

        UUID uuid = UUIDUtil.generateUUID(exclusions);
        assertNotEquals(exclusion, uuid);
    }
}
