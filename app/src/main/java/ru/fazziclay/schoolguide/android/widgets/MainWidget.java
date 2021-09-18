package ru.fazziclay.schoolguide.android.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

import ru.fazziclay.fazziclaylibs.FileUtils;

public class MainWidget extends AppWidgetProvider {
    public static final String WIDGETS_PATH = "/widgets.txt";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            FileUtils.write(context.getExternalFilesDir("").getAbsolutePath() + WIDGETS_PATH, FileUtils.read(context.getExternalFilesDir("").getAbsolutePath() +  WIDGETS_PATH) + "\n"+appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}