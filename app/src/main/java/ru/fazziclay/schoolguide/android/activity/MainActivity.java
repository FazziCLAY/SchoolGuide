package ru.fazziclay.schoolguide.android.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.fazziclay.fazziclaylibs.FileUtil;
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
        FileUtil.write(getExternalCacheDir().getAbsolutePath() + "/" + "author.txt", "Author: FazziCLAY\n* https://fazziclay.github.io\n* https://vk.com/fazziclay\n* https://t.me/fazziclay\n\nЭтот файл перезаписывается каждый раз когда вы входите в приложение. Автор: Миронов Станислав.");
        startService(new Intent(this, ForegroundService.class));
        startActivity(new Intent(this, HomeActivity.class));

        finish();
    }
}