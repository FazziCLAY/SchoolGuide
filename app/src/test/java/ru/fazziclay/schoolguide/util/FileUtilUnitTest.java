package ru.fazziclay.schoolguide.util;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.UUID;

public class FileUtilUnitTest {
    UUID uuid = UUID.randomUUID();
    String fileName = "unitTests/testFile.txt";

    @Test
    public void test() {
        FileUtil.write(fileName, uuid.toString());
        String read = FileUtil.read(fileName, "default111111");

        assertEquals(uuid.toString(), read);
    }
}
