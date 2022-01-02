package ru.fazziclay.schoolguide.data.settings;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.util.ColorUtils;

public class Settings extends BaseData {
    private static final String SETTINGS_FILE = "schoolguide.settings.json";
    private static final int CURRENT_FORMAT_VERSION = 6;

    public static Settings load(Context context) {
        Settings settings = (Settings) BaseData.load(context.getExternalFilesDir(null).getAbsolutePath().concat("/").concat(SETTINGS_FILE), Settings.class);
        settings.formatVersion = CURRENT_FORMAT_VERSION;
        settings.save();
        return settings;
    }

    public List<Integer> versionsHistory = new ArrayList<>();
    public DeveloperFeaturesSettings developerFeatures = new DeveloperFeaturesSettings();
    public VibrationSettings vibration = new VibrationSettings();
    public UUID selectedLocalSchedule = null;
    public NotificationStyleSettings notificationStyle = new NotificationStyleSettings();
    public int notifyBeforeTime = 3*60*60;

    public static class VibrationSettings {
        public boolean isLessonStart = true;
        public long[] lessonStartTact = SharedConstrains.VIBRATION_NOTIFY_LESSON;

        public boolean isLessonEnding = false;
        public long[] lessonEndingTact = SharedConstrains.VIBRATION_NOTIFY_LESSON;

        public boolean isRestStart = true;
        public long[] restStartTact = SharedConstrains.VIBRATION_NOTIFY_REST;

        public boolean isRestEnding = true;
        public long[] restEndingTact = SharedConstrains.VIBRATION_NOTIFY_REST_ENDING;

        public boolean isEnded = true;
        public long[] endedTact = SharedConstrains.VIBRATION_NOTIFY_END;
    }

    public static class DeveloperFeaturesSettings {
        public boolean developerGui = false;
        public boolean developerScheduleSync = false;
    }

    public static class NotificationStyleSettings {
        public boolean isUseCustomColor = false;
        public boolean colorized = false;
        public String color = ColorUtils.colorToHex(Color.CYAN);
        public NotificationClickAction clickAction = NotificationClickAction.TODAY_SCHEDULE;
        public boolean isScheduleInNotification = false;

        public enum NotificationClickAction {
            NONE(R.string.settings_notification_clickAction_none, (context, app) -> null),
            TODAY_SCHEDULE(R.string.settings_notification_clickAction_todaySchedule, (context, app) -> {
                //return PendingIntent.getActivity(context, 0, new Intent(context, TodayScheduleActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                return null;
            }),
            FULL_SCHEDULE(R.string.settings_notification_clickAction_fullSchedule, (context, app) -> {
                //Intent intent = new Intent(context, ScheduleEditActivity.class)
                //        .putExtra(ScheduleEditActivity.KEY_LOCAL_SCHEDULE_UUID, app.getSettings().selectedLocalSchedule.toString());
                //return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return null;
            });

            public int translate;
            public NotificationClickActionInterface startActivityInterface;
            NotificationClickAction(int stringKey, NotificationClickActionInterface startActivityInterface) {
                this.translate = stringKey;
                this.startActivityInterface = startActivityInterface;
            }

            public interface NotificationClickActionInterface {
                PendingIntent run(Context context, SchoolGuide app);
            }
        }
    }
}
