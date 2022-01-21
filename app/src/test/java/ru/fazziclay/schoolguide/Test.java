package ru.fazziclay.schoolguide;

import java.text.DateFormatSymbols;

import ru.fazziclay.schoolguide.app.scheduleinformator.android.PresetEditActivity;

public class Test {
    @org.junit.Test
    public void posToWeek() {
        int[] a = {
                0, 1, 2, 3, 4, 5, 6
        };
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        for (int aa : a) {
            int aaa = PresetEditActivity.posToWeek(true, aa);
            System.out.printf("a=%s, name=%s\n", aa, dateFormatSymbols.getWeekdays()[aaa]);
        }
    }
}
