package ru.fazziclay.schoolguide.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import ru.fazziclay.schoolguide.app.MilkLog;

/**
 * Набор утилит для работы с сетью
 * **/
public class NetworkUtil {
    private final static boolean DEBUG = false;
    /**
     * Спарсить данные со странички и вернуть их текстом (в основном для парсинга json-страниц)
     * @return содержание страницы
     * **/
    public static String parseTextPage(String url) throws IOException {
        if (DEBUG) MilkLog.g("NETWORK_UTIL (start) url=" + url);
        StringBuilder result = new StringBuilder();
        URL pageUrl = new URL(url);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pageUrl.openStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        bufferedReader.close();
        if (DEBUG) MilkLog.g("NETWORK_UTIL: (end) url=" + url + "; result=" + result);
        return result.toString();
    }
}
