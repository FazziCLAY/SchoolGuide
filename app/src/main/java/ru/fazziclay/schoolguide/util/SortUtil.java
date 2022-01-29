package ru.fazziclay.schoolguide.util;

/**
 * Набот утилит для сортировки
 * **/
public class SortUtil {
    public static void sort(Object[] list, Stringer stringer) {
        int n = list.length;
        Object temp;

        for (int i = 0; i < n; i++){
            for (int j = i + 1; j < n; j++) {
                Object oI = list[i];
                Object oJ = list[j];

                if (stringer.getString(oI).compareTo(stringer.getString(oJ)) > 0) {
                    temp = list[i];
                    list[i] = list[j];
                    list[j] = temp;
                }
            }
        }
    }

    public interface Stringer {
        String getString(Object o);
    }
}
