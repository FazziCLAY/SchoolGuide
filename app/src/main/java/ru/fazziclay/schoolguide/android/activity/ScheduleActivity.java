package ru.fazziclay.schoolguide.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.fazziclay.schoolguide.databinding.ActivitySettingsBinding;

public class ScheduleActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLayout();
    }

    private void initLayout() {

    }
}