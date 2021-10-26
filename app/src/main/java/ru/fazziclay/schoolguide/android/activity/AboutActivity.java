package ru.fazziclay.schoolguide.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.databinding.ActivityAboutBinding;

// DEV
public class AboutActivity extends AppCompatActivity {
    ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.activityTitle_about);

        initLayout();
    }

    private void initLayout() {

    }
}