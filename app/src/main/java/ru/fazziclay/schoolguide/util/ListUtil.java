package ru.fazziclay.schoolguide.util;

import java.util.List;

/**
 * Набор утили для работы со списками
 * **/
public class ListUtil {
    /**
     * Возвращает последний элемент списка, если длинна списка 0 то null
     * **/
    public static <T> T getLatestElement(List<T> list) {
        return list.size() <= 0 ? null : list.get(list.size()-1);
    }
}
