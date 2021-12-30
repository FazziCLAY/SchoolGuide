package ru.fazziclay.schoolguide.data.settings;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.android.activity.schedule.ScheduleEditActivity;
import ru.fazziclay.schoolguide.android.activity.schedule.TodayScheduleActivity;

public enum NotificationClickAction {
    NONE(R.string.settings_notification_clickAction_none, (context, app) -> null),
    TODAY_SCHEDULE(R.string.settings_notification_clickAction_todaySchedule, (context, app) -> {
        return PendingIntent.getActivity(context, 0, new Intent(context, TodayScheduleActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }),
    FULL_SCHEDULE(R.string.settings_notification_clickAction_fullSchedule, (context, app) -> {
        Intent intent = new Intent(context, ScheduleEditActivity.class)
                .putExtra(ScheduleEditActivity.KEY_LOCAL_SCHEDULE_UUID, app.getSettingsProvider().getSelectedLocalSchedule().toString());
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    });

    int stringKey;
    NotificationClickActionInterface startActivityInterface;
    NotificationClickAction(int stringKey, NotificationClickActionInterface startActivityInterface) {
        this.stringKey = stringKey;
        this.startActivityInterface = startActivityInterface;
    }

    public int getStringKey() {
        return stringKey;
    }

    public NotificationClickActionInterface getStartActivityInterface() {
        return startActivityInterface;
    }

    public interface NotificationClickActionInterface {
        PendingIntent run(Context context, SchoolGuide app);
    }
}
