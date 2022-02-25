package ru.fazziclay.schoolguide.app;

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

import ru.fazziclay.schoolguide.BuildConfig;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.app.scheduleinformator.ScheduleInformatorApp;
import ru.fazziclay.schoolguide.databinding.ErrorAppNullBinding;
import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;
import ru.fazziclay.schoolguide.datafixer.schem.Scheme37To50;
import ru.fazziclay.schoolguide.datafixer.schem.v33to35.SchemePre36To37;

public class SharedConstrains {
    public static final int APPLICATION_VERSION_CODE = BuildConfig.VERSION_CODE;
    public static final String APPLICATION_VERSION_NAME = BuildConfig.VERSION_NAME;
    public static final String APPLICATION_ID = BuildConfig.APPLICATION_ID;
    public static final String APPLICATION_BUILD_TYPE = BuildConfig.BUILD_TYPE;

    public static final String KEYS_V2 = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/main/manifest/v2/keys_v2.json";
    public static final String VERSION_MANIFEST_V2 = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/main/manifest/v2/version_manifest_v2.json";
    public static final String BUILTIN_SCHEDULE_V2 = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/main/manifest/v2/builtin_schedule_v2.json";

    /**
     * @since v50
     * **/
    public static final String APP_WIDGETS_LIST_FILE = "android_widgets_list.json";

    /**
     * Схемы востановления DataFixer`a
     * @see ru.fazziclay.schoolguide.datafixer.DataFixer
     * **/
    public static final AbstractScheme[] DATA_FIXER_SCHEMES = {
            new SchemePre36To37(),
            new Scheme37To50()
    };
    public static final long CRUTCH_INIT_DELAY = 1000 * 6;

    /**
     * Выдаёт каналы уведомлений которые нужно зарегистрировать
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

        NotificationChannel errors = new NotificationChannel(
                "errors",
                "errors",
                NotificationManager.IMPORTANCE_DEFAULT);
        updateCenter.setDescription("errors :/");
        notificationChannels.add(errors);

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
                    activity.getClass().getName()
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

    /*
    * 40 - fix translates
    * */
}
