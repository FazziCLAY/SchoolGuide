package ru.fazziclay.schoolguide.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import ru.fazziclay.schoolguide.SchoolGuide;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchoolGuide app = SchoolGuide.get(this);
        startService(new Intent(this, TickService.class));
    }
}
