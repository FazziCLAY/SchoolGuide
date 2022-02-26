package ru.fazziclay.schoolguide.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.fazziclay.schoolguide.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {
    ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.webView.loadUrl("https://github.com/FazziCLAY/SchoolGuide/blob/main/README.md#schoolguide");
    }
}