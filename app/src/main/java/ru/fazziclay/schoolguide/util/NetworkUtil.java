package ru.fazziclay.schoolguide.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
        if (DEBUG) Log.e("NETWORK_UTIL", "url=" + url);
        StringBuilder result = new StringBuilder();
        URL pageUrl = new URL(url);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pageUrl.openStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        bufferedReader.close();
        if (DEBUG) Log.e("NETWORK_UTIL", "url=" + url + "; result=" + result);
        return result.toString();
    }
}
