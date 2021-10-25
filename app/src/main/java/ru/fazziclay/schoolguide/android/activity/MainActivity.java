package ru.fazziclay.schoolguide.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

import ru.fazziclay.schoolguide.android.activity.schedule.ScheduleLessonEditActivity;
import ru.fazziclay.schoolguide.android.service.ForegroundService;
import ru.fazziclay.schoolguide.data.settings.DeveloperSettings;
import ru.fazziclay.schoolguide.data.settings.ExternalLoading;
import ru.fazziclay.schoolguide.data.settings.SettingsProvider;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            SettingsProvider preLoadSettingsProvider = new SettingsProvider(this);
            DeveloperSettings ds = preLoadSettingsProvider.getDeveloperSettings();

            if (!ds.isEnable || ds.startForegroundService) startService(new Intent(this, ForegroundService.class));
            if (!ds.isEnable || ds.startHomeActivity) startActivity(new Intent(this, HomeActivity.class));

            if (ds.isEnable) {
                if (ds.externalLoading) {
                    for (ExternalLoading externalLoading : ds.externalLoadings) {
                        Intent intent = new Intent(this, externalLoading.getActivity());

                        // I S   C O D E D   F E A T U R E S
                        if (externalLoading.isCodedFeatures()) {
                            intent.putExtra(ScheduleLessonEditActivity.KEY_LOCAL_SCHEDULE_UUID, preLoadSettingsProvider.getSelectedLocalSchedule().toString());
                            intent.putExtra(ScheduleLessonEditActivity.KEY_LOCAL_SCHEDULE_EDIT_DAY_OF_WEEK, Calendar.MONDAY);
                            //intent.putExtra(ScheduleLessonEditActivity.KEY_LESSON_POSITION, 0);
                        }

                        startActivity(intent);
                    }
                    return;
                }
            }
            finish();

        } catch (Throwable throwable) {
            Toast.makeText(this, "Error for starting!\n"+throwable.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}