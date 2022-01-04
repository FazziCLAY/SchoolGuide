package ru.fazziclay.schoolguide.app.multiplicationtrening;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import ru.fazziclay.schoolguide.app.SchoolGuideApp;

public class MultiplicationTreningApp {
    SchoolGuideApp app;
    Context context;

    public MultiplicationTreningApp(SchoolGuideApp app) {
        this.app = app;
    }

    public void launch(Activity activity) {
        activity.startActivity(new Intent(activity, MultiplicationTreningHomeActivity.class));
    }
}
