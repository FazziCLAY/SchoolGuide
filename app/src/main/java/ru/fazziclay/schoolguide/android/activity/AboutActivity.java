package ru.fazziclay.schoolguide.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.databinding.ActivityAboutBinding;

// DEV
public class AboutActivity extends AppCompatActivity {
    CrashReport crashReport;
    ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crashReport = new CrashReport(CrashReport.getFolder(this));
        try {
            binding = ActivityAboutBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setTitle(R.string.activityTitle_about);

            initLayout();
        } catch (Throwable throwable) {
            crashReport.error(throwable);
            crashReport.notifyUser(this);
            finish();
        }
    }

    private void initLayout() {

    }
}