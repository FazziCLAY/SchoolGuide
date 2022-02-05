package ru.fazziclay.schoolguide.util;

/**
 * Набот утилит для сортировки
 * **/
public class SortUtil {
    public static <T> void sort(T[] list, StringConsumer<T> stringConsumer) {
        int n = list.length;
        T temp;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                T oI = list[i];
                T oJ = list[j];

                String s1 = stringConsumer.get(oI);
                String s2 = stringConsumer.get(oJ);
                if (s1 == null) s1 = "";

                if (s1.compareTo(s2) > 0){
                    temp = list[i];
                    list[i] = list[j];
                    list[j] = temp;
                }
            }
        }
    }

    public interface StringConsumer<T> {
        String get(T o);
    }
}
