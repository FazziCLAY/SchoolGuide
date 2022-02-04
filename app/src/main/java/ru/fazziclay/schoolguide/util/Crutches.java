package ru.fazziclay.schoolguide.util;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class Crutches {
    /**
     * Уходит в цикл если инстанс приложения не доступен, к цикле максимум крутится 5 секунд, потом выходит из цикла.
     * **/
    public static void appInitializationDelay(long maxWait) {
        long startCrutch = System.currentTimeMillis();
        while (!SchoolGuideApp.isInstanceAvailable()) {
            if (System.currentTimeMillis() - startCrutch > maxWait) break;
        }
    }
}
