package ru.fazziclay.schoolguide.android.activity.developer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.data.manifest.Manifest;
import ru.fazziclay.schoolguide.data.schedule.Schedule;
import ru.fazziclay.schoolguide.databinding.ActivityDeveloperBinding;

public class DeveloperActivity extends AppCompatActivity {
    SchoolGuide app;
    ActivityDeveloperBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuide.get(this);
        binding = ActivityDeveloperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setThisScheduleToDeveloperScheduleInManifest.setOnClickListener(ignore -> {
            Schedule thisSchedule = app.getSchedule().getSchedule();
            app.getManifestProvider().setDeveloperSchedule(thisSchedule.copy());
            Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
        });

        binding.saveManifest.setOnClickListener(ignore -> {
            app.getManifestProvider().save();
            Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
        });

        binding.loadManifest.setOnClickListener(ignore -> {
            app.getManifestProvider().setManifest((Manifest) app.getManifestProvider().load());
            Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
        });

        binding.openManifestUtil.setOnClickListener(ignore -> startActivity(new Intent(this, DeveloperManifestUtilsActivity.class)));

        binding.sendUpdateCheckerNotify.setOnClickListener(ignore -> {
            app.sendUpdateCheckerNotify();
            Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
        });

        binding.testCrash.setOnClickListener(ignore -> {
            new CrashReport(this, new Exception("This a test crash report in developer activity; randomInteger="+new Random().nextInt()));
            Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
        });
    }
}