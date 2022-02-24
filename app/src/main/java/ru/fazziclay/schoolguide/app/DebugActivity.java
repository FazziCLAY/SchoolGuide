package ru.fazziclay.schoolguide.app;

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

    /*private*/ SchoolGuideApp app;
    private ActivityDebugBinding binding;
    /*private*/ AppTrace appTrace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        if (app == null) {
            setContentView(SharedConstrains.getAppNullView(this));
            return;
        }
        appTrace = app.getAppTrace();
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

        binding.inputFloor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                try {
                    double a = Math.floor(Double.parseDouble(input));
                    binding.outputFloor.setText(String.valueOf(a));
                } catch (Exception e) {
                    binding.outputFloor.setText(e.toString());
                }
            }
        });


        binding.debugCallbackSend.setOnClickListener(ignore -> {
            String data = binding.debugCallbackData.getText().toString();
            app.getDebugSignalListenerCallbacks().run(((callbackStorage, callback) -> callback.run(data)));
        });


        binding.pendingUpdateGlobalSend.setOnClickListener(ignore -> {
            boolean startupMode = binding.pendingUpdateGlobalStartupMode.isChecked();
            app.pendingUpdateGlobal(startupMode);
        });


        binding.milkLogSend.setOnClickListener(ignore -> MilkLog.g(binding.milkLogText.getText().toString()));
    }
}