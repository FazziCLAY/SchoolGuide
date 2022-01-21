package ru.fazziclay.schoolguide.util;

import org.junit.Test;

import java.util.Arrays;

public class SortUtilTest {
    @Test
    public void test() {
        String[] array = {
                "10",
                "10a",
                "10 - 0",
                "10 - 1",
                "10 - 10",
                "10 - 11",
                "hello",
                "original",
                "copy",
                "amongus",
                "abobus",
                "abama",
                "fuck",
                "finger",
                "world",
                "flight",
                "привет",
                "оригинал",
                "копия",
                "амонгус",
                "абобус",
                "абама",
                "фак",
                "мир",
                "летатель"
        };
        System.out.println("original:" + Arrays.toString(array));

        SortUtil.sort(array, o -> (String) o);

        System.out.println("sorted: "  + Arrays.toString(array));
    }
}
