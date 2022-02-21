package ru.fazziclay.schoolguide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;
import ru.fazziclay.schoolguide.app.scheduleinformator.android.PresetListActivity;

/**
 * <p>Активити которое запускается первым, оно запускает всю систему если она ещё не запущена, и запускает нужный
 * активити</p>
 *
 * <p>Оно ставит тему на тёмную</p>
 * <p>ставит на экран текст названия приложения</p>
 * <p>если андроид позволяет: регистрирует каналы уведомлений {@link SchoolGuideApp#registerNotificationChannels(Context)}</p>
 * <p>вызывает {@link SchoolGuideApp#get(Context)}</p>
 * <p>запускает {@link PresetListActivity}</p>
 * **/
public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dark theme only
        Exception setDarkThemeException = null;
        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } catch (Exception e) {
            Log.e("Dark theme", "error to set defaultNightMode to YES", e);
            setDarkThemeException = e;
        }

        // Loading
        SchoolGuideApp app = SchoolGuideApp.get(this);
        if (app == null) {
            setContentView(SharedConstrains.getAppNullView(this));
            return;
        }

        if (setDarkThemeException != null) {
            app.getAppTrace().point("Set theme exception!", setDarkThemeException);
        }

        startActivity(PresetListActivity.getLaunchIntent(this));
        finish();
    }
}
