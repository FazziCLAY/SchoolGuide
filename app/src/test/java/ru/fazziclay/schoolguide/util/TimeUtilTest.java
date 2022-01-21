package ru.fazziclay.schoolguide.util;

import org.junit.Test;

import static org.junit.Assert.*;

import ru.fazziclay.schoolguide.util.time.ConvertMode;
import ru.fazziclay.schoolguide.util.time.HumanTimeType;
import ru.fazziclay.schoolguide.util.time.TimeUtil;

/**
 * Example local unit test, which will execute on the development machine (host).
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TimeUtilTest {
    @Test
    public void humanValuesException() {
        assertThrows(RuntimeException.class, () -> TimeUtil.getHumanValue(0, null));
        assertThrows("Unknown", RuntimeException.class, () -> TimeUtil.getHumanValue(0, null));
    }

    @Test
    public void humanValues() {
        final int DAY = 24 * 60 * 60 - 1;

        int a = TimeUtil.getHumanValue(DAY, HumanTimeType.HOUR);
        assertEquals(23, a);

        a = TimeUtil.getHumanValue(DAY, HumanTimeType.MINUTE_OF_HOUR);
        assertEquals(59, a);

        a = TimeUtil.getHumanValue(DAY, HumanTimeType.SECONDS_OF_MINUTE);
        assertEquals(59, a);
    }

    @Test
    public void convertToHuman() {
        final int DAY = 24 * 60 * 60 - 1;

        String a = TimeUtil.convertToHumanTime(DAY, ConvertMode.HHMMSS);
        assertEquals("23:59:59", a);

        a = TimeUtil.convertToHumanTime(DAY, ConvertMode.HHMM);
        assertEquals("23:59", a);

        a = TimeUtil.convertToHumanTime(DAY, ConvertMode.hhMMSS);
        assertEquals("23:59:59", a);

        a = TimeUtil.convertToHumanTime(60*60, ConvertMode.hhMMSS);
        assertEquals("01:00:00", a);

        a = TimeUtil.convertToHumanTime(60*60-1, ConvertMode.hhMMSS);
        assertEquals("59:59", a);
    }

    @Test
    public void debugPrint() {
        System.out.println("-- Day Seconds & seconds->toHuman --");
        System.out.println(TimeUtil.getDaySeconds());
        System.out.println(TimeUtil.convertToHumanTime(TimeUtil.getDaySeconds(), ConvertMode.HHMMSS));

        System.out.println("-- Week Seconds & seconds->toHuman --");

        System.out.println(TimeUtil.getWeekSeconds());
        System.out.println(TimeUtil.convertToHumanTime(TimeUtil.getWeekSeconds(), ConvertMode.HHMMSS));
    }
}
