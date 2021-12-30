package ru.fazziclay.schoolguide.data.settings;

import android.graphics.Color;

import ru.fazziclay.schoolguide.util.ColorUtils;

public class NotificationStyle {
    boolean colorized = false;
    String color = ColorUtils.colorToHex(Color.CYAN);
    NotificationClickAction clickAction = NotificationClickAction.TODAY_SCHEDULE;

    transient int cachedColor = 0;
    transient boolean isCachedColor = false;

    public void updateCache() {
        cachedColor = ColorUtils.parseColor(color, ColorUtils.STRING_CYAN);
        isCachedColor = true;
    }

    public boolean isColorized() {
        return colorized;
    }

    public void setColorized(boolean colorized) {
        this.colorized = colorized;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public NotificationClickAction getClickAction() {
        return clickAction;
    }

    public void setClickAction(NotificationClickAction clickAction) {
        this.clickAction = clickAction;
    }

    public int getCachedColor() {
        return cachedColor;
    }

    public void setCachedColor(int cachedColor) {
        this.cachedColor = cachedColor;
    }

    public boolean isCachedColor() {
        return isCachedColor;
    }

    public void setCachedColor(boolean cachedColor) {
        isCachedColor = cachedColor;
    }
}
