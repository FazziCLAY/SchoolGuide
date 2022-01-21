package ru.fazziclay.schoolguide.util;

import java.util.List;

public class ListUtil {
    public static <T> T getLatestElement(List<T> list) {
        return list.size() <= 0 ? null : list.get(list.size()-1);
    }
}
