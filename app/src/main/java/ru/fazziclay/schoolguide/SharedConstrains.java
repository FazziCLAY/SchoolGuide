package ru.fazziclay.schoolguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.databinding.ErrorAppNullBinding;
import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.datafixer.schem.v33to35.SchemePre36To37;

public class SharedConstrains {
    public static final int APPLICATION_VERSION_CODE = BuildConfig.VERSION_CODE;
    public static final String APPLICATION_VERSION_NAME = BuildConfig.VERSION_NAME;
    public static final String APPLICATION_ID = BuildConfig.APPLICATION_ID;
    public static final String APPLICATION_BUILD_TYPE = BuildConfig.BUILD_TYPE;

    // TODO: 2022-01-26 change to main branch
    public static final String KEYS_V2 = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/dev/v34/manifest/v2/keys_v2.json";
    public static final String VERSION_MANIFEST_V2 = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/dev/v34/manifest/v2/version_manifest_v2.json";
    public static final String BUILTIN_SCHEDULE_V2 = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/dev/v34/manifest/v2/builtin_schedule_v2.json";

    /**
     * Схемы востановления DataFixer`a
     * @see ru.fazziclay.schoolguide.datafixer.DataFixer
     * **/
    public static final AbstractScheme[] DATA_FIXER_SCHEMES = {
            new SchemePre36To37()
    };
    public static final long CRUTCH_INIT_DELAY = 1000 * 6;

    /**
     * Выдаёт каналы которые нужно зарегистрировать
     * @see SchoolGuideApp#registerNotificationChannels(Context)
     * **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<NotificationChannel> getNotificationChannels(Context context) {
        final List<NotificationChannel> notificationChannels = new ArrayList<>();

        NotificationChannel scheduleInformatorNone = new NotificationChannel(
                ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NONE,
                context.getString(R.string.notificationChannel_scheduleInformator_scheduleNone_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        scheduleInformatorNone.setDescription(context.getString(R.string.notificationChannel_scheduleInformator_scheduleNone_description));
        notificationChannels.add(scheduleInformatorNone);

        NotificationChannel scheduleInformatorNext = new NotificationChannel(
                ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NEXT,
                context.getString(R.string.notificationChannel_scheduleInformator_scheduleNext_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        scheduleInformatorNext.setDescription(context.getString(R.string.notificationChannel_scheduleInformator_scheduleNext_description));
        notificationChannels.add(scheduleInformatorNext);

        NotificationChannel scheduleInformatorNow = new NotificationChannel(
                ScheduleInformatorApp.NOTIFICATION_CHANNEL_ID_NOW,
                context.getString(R.string.notificationChannel_scheduleInformator_scheduleNow_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        scheduleInformatorNow.setDescription(context.getString(R.string.notificationChannel_scheduleInformator_scheduleNow_description));
        notificationChannels.add(scheduleInformatorNow);

        NotificationChannel updateCenter = new NotificationChannel(
                UpdateCenterActivity.NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notificationChannel_updateCenter_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        updateCenter.setDescription(context.getString(R.string.notificationChannel_updateCenter_description));
        notificationChannels.add(updateCenter);

        return notificationChannels;
    }

    /**
     * Если {@link SchoolGuideApp#get()} выдал null то ставить setContentView
     * **/
    @SuppressLint("SetTextI18n")
    public static View getAppNullView(Activity activity) {
        try {
            ErrorAppNullBinding binding = ErrorAppNullBinding.inflate(activity.getLayoutInflater());

            binding.errorText.setText(activity.getString(
                    R.string.error_appNull_message,
                    String.valueOf(SharedConstrains.APPLICATION_VERSION_CODE),
                    String.valueOf(ErrorCode.ERROR_APP_GET_RETURN_NULL),
                    activity == null ? "null" : activity.getClass().getName()
            ));

            return binding.getRoot();
        } catch (Exception e) {
            try {
                TextView textView = new TextView(activity);
                textView.setText("SchoolGuide Error\n"+ e);
                return textView;

            } catch (Exception ignored) {
                return null;
            }
        }
    }
}
