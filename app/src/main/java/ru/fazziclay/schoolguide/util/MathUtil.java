package ru.fazziclay.schoolguide.util;

import java.util.Random;

public class MathUtil {
    /**
     * <h1>Сгерерировать случайное число</h1>
     * @apiNote если перепутать MAX и MIN все отработает нормально!
     * **/
    public static int random(Random random, int min, int max) {
        if (min > max) {
            int tempMin = min;
            min = max;
            max = tempMin;
        }
        return random.nextInt(max + 1 - min) + min;
    }

    /**
     * <h1>Округлить число d до n знаков</h1>
     * **/
    public static double round(double d, int n) {
        double f = Math.pow(10, n);
        return Math.round(d * f) / f;
    }

    /**
     * <h1>Найти среднее арифметичское из массива d</h1>
     * **/
    public static double average(long[] d) {
        long sum = 0;
        int len = 0;
        for (long dd : d) {
            if (dd == 0) continue;
            sum += dd;
            len++;
        }

        double a = ((double) sum) / ((double)len);
        return sum == 0 ? 0 : a;
    }
}
