package ru.fazziclay.schoolguide.android.activity;

import android.app.Activity;
import android.os.Bundle;

import ru.fazziclay.schoolguide.SchoolGuide;

// Main Activity
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchoolGuide app = SchoolGuide.get(this);
        app.loadAndroidApp(this);
        //finish();
    }
}