package ru.fazziclay.schoolguide.android.activity.developer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.fazziclay.schoolguide.SchoolGuide;
import ru.fazziclay.schoolguide.data.manifest.Manifest;
import ru.fazziclay.schoolguide.data.schedule.Schedule;
import ru.fazziclay.schoolguide.databinding.ActivityDeveloperBinding;

public class DeveloperActivity extends AppCompatActivity {
    ActivityDeveloperBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeveloperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setThisScheduleToDeveloperScheduleInManifest.setOnClickListener(ignore -> {
            if (!SchoolGuide.isInstanceAvailable()) {
                Toast.makeText(this, "SchoolGuide instance not available!", Toast.LENGTH_SHORT).show();
                return;
            }
            Schedule thisSchedule = SchoolGuide.getInstance().getScheduleProvider().getSchedule();
            SchoolGuide.getInstance().getManifestProvider().setDeveloperSchedule(thisSchedule.copy());
            Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
        });

        binding.saveManifest.setOnClickListener(ignore -> {
            if (!SchoolGuide.isInstanceAvailable()) {
                Toast.makeText(this, "SchoolGuide instance not available!", Toast.LENGTH_SHORT).show();
                return;
            }
            SchoolGuide.getInstance().getManifestProvider().save();
            Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
        });

        binding.loadManifest.setOnClickListener(ignore -> {
            if (!SchoolGuide.isInstanceAvailable()) {
                Toast.makeText(this, "SchoolGuide instance not available!", Toast.LENGTH_SHORT).show();
                return;
            }
            SchoolGuide.getInstance().getManifestProvider().setManifest((Manifest) SchoolGuide.getInstance().getManifestProvider().load());
            Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
        });

        binding.openManifestUtil.setOnClickListener(ignore -> startActivity(new Intent(this, DeveloperManifestUtilsActivity.class)));

        binding.sendUpdateCheckerNotify.setOnClickListener(ignore -> {
            if (!SchoolGuide.isInstanceAvailable()) {
                Toast.makeText(this, "SchoolGuide instance not available!", Toast.LENGTH_SHORT).show();
                return;
            }
            SchoolGuide.getInstance().sendUpdateCheckerNotify();
            Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
        });
    }
}