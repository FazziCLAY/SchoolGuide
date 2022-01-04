package ru.fazziclay.schoolguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class LaunchActivity extends Activity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } catch (Exception ignored) {}
        TextView loading = new TextView(this);
        loading.setTextSize(40);
        loading.setGravity(Gravity.CENTER);
        loading.setText("SchoolGuide");
        setContentView(loading);
        SchoolGuideApp.get().launchAndroidApp(this, this);
    }
}
