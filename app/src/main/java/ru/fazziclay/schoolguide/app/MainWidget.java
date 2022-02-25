package ru.fazziclay.schoolguide.app;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

import java.io.File;

import ru.fazziclay.schoolguide.util.DataUtil;

public class MainWidget extends AppWidgetProvider {
    private AppWidgetsList appWidgetsList;
    private SchoolGuideApp app;
    private boolean appWidgetsFromApp;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        get(context);
        appWidgetsList.onWidgetsUpdate(appWidgetIds);
        save(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        get(context);
        appWidgetsList.onWidgetsDelete(appWidgetIds);
        save(context);
    }

    @Override
    public void onEnabled(Context context) {
        get(context);
        appWidgetsList.setEnableStatus(true);
        save(context);
        if (app != null) app.getWidgetsEnableStatusChangeListenerCallbacks().run(((callbackStorage, callback) -> callback.onChange(true)));
    }

    @Override
    public void onDisabled(Context context) {
        get(context);
        appWidgetsList.setEnableStatus(false);
        save(context);
        if (app != null) app.getWidgetsEnableStatusChangeListenerCallbacks().run(((callbackStorage, callback) -> callback.onChange(false)));
    }

    private AppWidgetsList getInFile(Context context) {
        File listFile = new File(context.getExternalFilesDir(null), SharedConstrains.APP_WIDGETS_LIST_FILE);
        return DataUtil.load(listFile, AppWidgetsList.class);
    }

    private void get(Context context) {
        if (SchoolGuideApp.isInstanceAvailable()) {
            SchoolGuideApp app = SchoolGuideApp.get();
            if (app == null) {
                appWidgetsFromApp = false;
                appWidgetsList = getInFile(context);
                this.app = null;

            } else {
                appWidgetsFromApp = true;
                appWidgetsList = app.getAppWidgetsList();
                this.app = app;
            }

        } else {
            appWidgetsFromApp = false;
            appWidgetsList = getInFile(context);
            this.app = null;
        }
    }

    private void save(Context context) {
        if (appWidgetsFromApp) {
            if (app != null) app.saveAppWidgetsList();
        } else {
            File listFile = new File(context.getExternalFilesDir(null), SharedConstrains.APP_WIDGETS_LIST_FILE);
            DataUtil.save(listFile, appWidgetsList);
        }
    }
}