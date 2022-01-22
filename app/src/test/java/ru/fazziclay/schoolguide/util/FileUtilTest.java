package ru.fazziclay.schoolguide.util;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.UUID;

public class FileUtilTest {
    UUID uuid = UUID.randomUUID();
    String fileName = "unit_test/fileUtil.txt";

    @Test
    public void test() {
        FileUtil.write(fileName, uuid.toString());
        String read = FileUtil.read(fileName, "default");

        assertEquals(uuid.toString(), read);
    }
}
