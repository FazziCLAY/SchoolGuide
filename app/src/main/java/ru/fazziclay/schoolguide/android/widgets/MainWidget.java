package ru.fazziclay.schoolguide.android.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import ru.fazziclay.fazziclaylibs.FileUtils;
import ru.fazziclay.schoolguide.R;

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

    public static void updateAllWidgets(Context context, String text) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);
        views.setTextViewText(R.id.main_text, text);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        String[] widgetsIds = FileUtils.read(context.getExternalFilesDir("") + MainWidget.WIDGETS_PATH).split("\n");
        int i = 0;
        while (i < widgetsIds.length) {
            try {
                appWidgetManager.updateAppWidget(Integer.parseInt(widgetsIds[i]), views);
            } catch (Exception ignored) {}
            i++;
        }
    }
}