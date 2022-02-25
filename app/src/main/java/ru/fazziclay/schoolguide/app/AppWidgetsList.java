package ru.fazziclay.schoolguide.app;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AppWidgetsList {
    /**
     * @since v50
     * Name "widgetsIds" since v50
     * **/
    @SerializedName("widgetsIds")
    private List<Integer> widgetsIds = new ArrayList<>();

    /**
     * @since v50
     * Name "enableStatus" since v50
     * **/
    @SerializedName("enableStatus")
    private boolean enableStatus;

    private void fix() {
        if (widgetsIds == null) widgetsIds = new ArrayList<>();
    }

    public void onWidgetsUpdate(int[] appWidgetsIds) {
        fix();
        for (int appWidgetId : appWidgetsIds) {
            if (!widgetsIds.contains(appWidgetId)) {
                widgetsIds.add(appWidgetId);
            }
        }
    }

    public void onWidgetsDelete(int[] appWidgetsIds) {
        fix();
        for (int appWidgetId : appWidgetsIds) {
            if (widgetsIds.contains(appWidgetId)) {
                widgetsIds.remove((Integer) appWidgetId);
            }
        }
    }

    public void setEnableStatus(boolean enableStatus) {
        this.enableStatus = enableStatus;
    }

    public boolean isEnableStatus() {
        return enableStatus;
    }

    public Integer[] getWidgetsIds() {
        return widgetsIds.toArray(new Integer[0]);
    }
}
