package ru.fazziclay.schoolguide;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

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
        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } catch (Exception ignored) {}

        // Loading text
        TextView loadingTextView = new TextView(this);
        loadingTextView.setGravity(Gravity.CENTER);
        loadingTextView.setTextSize(40);
        loadingTextView.setTextColor(Color.WHITE);
        loadingTextView.setText(R.string.application_name);

        setContentView(loadingTextView);

        // Notification channels
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            SchoolGuideApp.registerNotificationChannels(this);
        }

        // Loading
        SchoolGuideApp.get(this);
        startActivity(PresetListActivity.getLaunchIntent(this));

        finish();
    }
}
