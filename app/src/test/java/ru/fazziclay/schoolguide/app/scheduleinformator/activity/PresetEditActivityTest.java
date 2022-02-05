package ru.fazziclay.schoolguide.app.scheduleinformator.activity;

import org.junit.Test;

import java.text.DateFormatSymbols;

import ru.fazziclay.schoolguide.app.scheduleinformator.android.PresetEditActivity;

public class PresetEditActivityTest {
    @Test
    public void posToWeek() {
        int[] a = {0, 1, 2, 3, 4, 5, 6};
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        for (int i : a) {
            int weekDay = PresetEditActivity.posToWeek(true, i);
            System.out.printf("a=%s, name=%s\n", i, dateFormatSymbols.getWeekdays()[weekDay]);
        }
    }
}
