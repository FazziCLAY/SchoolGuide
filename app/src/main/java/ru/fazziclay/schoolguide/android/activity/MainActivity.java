package ru.fazziclay.schoolguide.android.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.fazziclay.schoolguide.Config;
import ru.fazziclay.schoolguide.android.service.ForegroundService;

public class MainActivity extends AppCompatActivity {
    Thread loadingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingThread = new Thread(this::loading);
        loadingThread.start();
    }

    public void loading() {
        Config.init(this);
        startService(new Intent(this, ForegroundService.class));
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}