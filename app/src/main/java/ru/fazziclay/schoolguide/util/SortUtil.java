package ru.fazziclay.schoolguide.util;

/**
 * Набот утилит для сортировки
 * **/
public class SortUtil {
    /**
     * Перерасположить(сортировать) массив list по его имиени, имя достаётся с помощью {@link SortStringConsumer}
     * **/
    public static <T> void sortByName(T[] list, SortStringConsumer<T> consumer) {
        int n = list.length;
        T temp;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                T oI = list[i];
                T oJ = list[j];

                String s1 = consumer.get(oI);
                String s2 = consumer.get(oJ);
                if (s1 == null) s1 = "";
                if (s2 == null) s2 = "";

                if (s1.compareTo(s2) > 0) {
                    temp = list[i];
                    list[i] = list[j];
                    list[j] = temp;
                }
            }
        }
    }

    public interface SortStringConsumer<T> {
        String get(T o);
    }
}
