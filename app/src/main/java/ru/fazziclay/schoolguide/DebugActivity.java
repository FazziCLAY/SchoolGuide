package ru.fazziclay.schoolguide;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import ru.fazziclay.schoolguide.databinding.ActivityDebugBinding;
import ru.fazziclay.schoolguide.util.ColorUtil;

public class DebugActivity extends AppCompatActivity {
    ActivityDebugBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDebugBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textColorizeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                binding.textColorizeResult.setText(ColorUtil.colorize(input, Color.CYAN, Color.RED, Typeface.ITALIC));
            }
        });
    }
}