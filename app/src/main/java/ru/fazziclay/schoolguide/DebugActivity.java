package ru.fazziclay.schoolguide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;

import ru.fazziclay.schoolguide.databinding.ActivityDebugBinding;
import ru.fazziclay.schoolguide.util.AppTrace;
import ru.fazziclay.schoolguide.util.ColorUtil;

public class DebugActivity extends AppCompatActivity {
    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, DebugActivity.class);
    }

    ActivityDebugBinding binding;
    AppTrace trace = new AppTrace();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trace.trace("onCreate");
        binding = ActivityDebugBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textColorizeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                binding.textColorizeResult.setText(ColorUtil.colorize(input, Color.CYAN, Color.RED, Typeface.ITALIC));
            }
        });
        trace.trace("preSave");
        AppTrace.saveAndLog(this, trace);
    }
}