package ru.fazziclay.schoolguide.app;

import ru.fazziclay.schoolguide.app.scheduleinformator.android.PresetEditActivity;

public class Settings {
    /**
     * Показывать ли статус выбранного пресета в уведомлении
     * */
    private boolean isNotification = true;
    /**
     * Активировать фишки разработчика
     * */
    private boolean isDeveloperFeatures = false;
    /**
     * За сколько секунд до начала события считать его не пустым
     * */
    private int notificationStatusBeforeTime = 2*60*60;
    /**
     * Скрывать пустое уведомление
     * */
    private boolean isStopForegroundIsNone = true;
    /**
     * Первый день недели понидельник
     * */
    private boolean isFirstMonday = true;
    /**
     * Помещать название события на следующую линию в редакторе пресета
     * */
    private boolean isPresetEditEventNameInNextLine = false;
    /**
     * Цветовая схема редактора пресетов
     * */
    private PresetEditActivity.ColorScheme presetEditColorScheme = PresetEditActivity.ColorScheme.DEFAULT;
    /**
     * Встроенный список пресетов в списке
     * */
    private boolean isBuiltinPresetList = false;

    /**
     * @see Settings#isNotification
     * **/
    public boolean isNotification() {
        return isNotification;
    }

    /**
     * @see Settings#isNotification
     * **/
    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    /**
     * @see Settings#isDeveloperFeatures
     * **/
    public boolean isDeveloperFeatures() {
        return isDeveloperFeatures;
    }

    /**
     * @see Settings#isDeveloperFeatures
     * **/
    public void setDeveloperFeatures(boolean developerFeatures) {
        isDeveloperFeatures = developerFeatures;
    }

    /**
     * @see Settings#notificationStatusBeforeTime
     * **/
    public int getNotificationStatusBeforeTime() {
        return notificationStatusBeforeTime;
    }

    /**
     * @see Settings#notificationStatusBeforeTime
     * **/
    public void setNotificationStatusBeforeTime(int notificationStatusBeforeTime) {
        this.notificationStatusBeforeTime = notificationStatusBeforeTime;
    }

    /**
     * @see Settings#isStopForegroundIsNone
     * **/
    public boolean isHideEmptyNotification() {
        return isStopForegroundIsNone;
    }

    /**
     * @see Settings#isStopForegroundIsNone
     * **/
    public void setIsHideEmptyNotification(boolean stopForegroundIsNone) {
        isStopForegroundIsNone = stopForegroundIsNone;
    }

    /**
     * @see Settings#isFirstMonday
     * **/
    public boolean isFirstMonday() {
        return isFirstMonday;
    }

    /**
     * @see Settings#isFirstMonday
     * **/
    public void setFirstMonday(boolean firstMonday) {
        isFirstMonday = firstMonday;
    }

    /**
     * @see Settings#isPresetEditEventNameInNextLine
     * **/
    public boolean isPresetEditEventNameInNextLine() {
        return isPresetEditEventNameInNextLine;
    }

    /**
     * @see Settings#isPresetEditEventNameInNextLine
     * **/
    public void setPresetEditEventNameInNextLine(boolean presetEditEventNameInNextLine) {
        isPresetEditEventNameInNextLine = presetEditEventNameInNextLine;
    }

    /**
     * @see Settings#presetEditColorScheme
     * **/
    public PresetEditActivity.ColorScheme getPresetEditColorScheme() {
        return presetEditColorScheme;
    }

    /**
     * @see Settings#presetEditColorScheme
     * **/
    public void setPresetEditColorScheme(PresetEditActivity.ColorScheme presetEditColorScheme) {
        this.presetEditColorScheme = presetEditColorScheme;
    }

    /**
     * @see Settings#isBuiltinPresetList
     * **/
    public boolean isBuiltinPresetList() {
        return isBuiltinPresetList;
    }

    /**
     * @see Settings#isBuiltinPresetList
     * **/
    public void setBuiltinPresetList(boolean builtinPresetList) {
        isBuiltinPresetList = builtinPresetList;
    }
}
