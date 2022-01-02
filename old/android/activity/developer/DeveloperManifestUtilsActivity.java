package ru.fazziclay.schoolguide.android.activity.developer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import ru.fazziclay.schoolguide.data.manifest.AppVersion;
import ru.fazziclay.schoolguide.data.manifest.Manifest;
import ru.fazziclay.schoolguide.data.manifest.ManifestProvider;
import ru.fazziclay.schoolguide.databinding.ActivityDeveloperManifestUtilsBinding;

public class DeveloperManifestUtilsActivity extends AppCompatActivity {
    ActivityDeveloperManifestUtilsBinding binding;
    Manifest manifest = null;

    int manifestKey = 0;
    boolean isTechnicalWorks = true;
    AppVersion latestVersion = null;
    HashMap<String, String> latestVersionChangelog = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeveloperManifestUtilsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLayout();
    }

    private void initLayout() {
        binding.manifestKey.setText(String.valueOf(manifestKey));
        binding.isTechnicalWorks.setChecked(isTechnicalWorks);

        binding.latestVersionAddChangelogDelButton.setOnClickListener(ign -> {
            String key = binding.latestVersionAddChangelogLang.getText().toString();
            latestVersionChangelog.remove(key);
        });

        binding.latestVersionAddChangelogAddButton.setOnClickListener(ign -> {
            String key = binding.latestVersionAddChangelogLang.getText().toString();
            String text = binding.latestVersionAddChangelogText.getText().toString();
            latestVersionChangelog.put(key, text);
        });

        binding.getResult.setOnClickListener(ignore -> {
            try {
                manifestKey = Integer.parseInt(binding.manifestKey.getText().toString());
                isTechnicalWorks = binding.isTechnicalWorks.isChecked();
                latestVersionChangelog.put("default", binding.latestVersionDefaultChangelog.getText().toString());

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                latestVersion = new AppVersion(
                        Integer.parseInt(binding.latestVersionCode.getText().toString()),
                        binding.latestVersionName.getText().toString(),
                        binding.latestVersionPageUrl.getText().toString(),
                        binding.latestVersionDownloadUrl.getText().toString(),
                        latestVersionChangelog
                );
                manifest = new Manifest(manifestKey, isTechnicalWorks, latestVersion);
                manifest.formatVersion = ManifestProvider.CURRENT_FORMAT_VERSION;


                binding.resultJson.setText(gson.toJson(manifest, Manifest.class));
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}