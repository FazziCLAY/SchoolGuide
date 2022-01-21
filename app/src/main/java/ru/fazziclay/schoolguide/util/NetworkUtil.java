package ru.fazziclay.schoolguide.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class NetworkUtil {
    public static String parseTextPage(String url) throws IOException {
        StringBuilder result = new StringBuilder();
        URL pageUrl = new URL(url);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pageUrl.openStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        bufferedReader.close();
        return result.toString();
    }
}
