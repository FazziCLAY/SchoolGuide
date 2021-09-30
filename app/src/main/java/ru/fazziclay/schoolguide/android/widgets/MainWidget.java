package ru.fazziclay.schoolguide.android.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import ru.fazziclay.fazziclaylibs.FileUtil;
import ru.fazziclay.schoolguide.R;

public class MainWidget extends AppWidgetProvider {
    public static final String WIDGETS_FILE = "widgets.sgpltxt";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            addWidget(context, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            removeWidget(context, appWidgetId);
        }
    }

    public static String getWidgetsFilePath(Context context) {
        return context.getExternalFilesDir("").getAbsolutePath() + "/" + WIDGETS_FILE;
    }

    public static void addWidget(Context context, int widgetId) {
        String fileContent = FileUtil.read(getWidgetsFilePath(context), "");
        fileContent = fileContent+widgetId+"&";

        FileUtil.write(getWidgetsFilePath(context), fileContent);
    }

    public static void removeWidget(Context context, int widgetId) {
        String fileContent = FileUtil.read(getWidgetsFilePath(context), "");
        fileContent = fileContent.replace(widgetId+"&", "");

        FileUtil.write(getWidgetsFilePath(context), fileContent);
    }

    public static void updateAllWidgets(Context context, String text) {
        String fileContent = FileUtil.read(getWidgetsFilePath(context), "");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);
        views.setTextViewText(R.id.main_text, text);
        String[] widgetsIds = fileContent.split("&");
        int i = 0;
        while (i < widgetsIds.length) {
            int id;
            try {
                id = Integer.parseInt(widgetsIds[i]);
            } catch (Exception ignored) {
                return;
            }
            appWidgetManager.updateAppWidget(id, views);
            i++;
        }
    }
}