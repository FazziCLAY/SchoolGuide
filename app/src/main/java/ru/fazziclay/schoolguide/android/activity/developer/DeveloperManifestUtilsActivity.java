package ru.fazziclay.schoolguide.android.activity.developer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.fazziclay.schoolguide.databinding.ActivityDeveloperManifestUtilsBinding;

public class DeveloperManifestUtilsActivity extends AppCompatActivity {
    ActivityDeveloperManifestUtilsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeveloperManifestUtilsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLayout();
    }

    private void initLayout() {

    }
}