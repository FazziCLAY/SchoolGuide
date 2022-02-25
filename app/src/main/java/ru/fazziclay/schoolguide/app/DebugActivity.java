package ru.fazziclay.schoolguide.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;

import androidx.appcompat.app.AppCompatActivity;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.databinding.ActivityDebugBinding;
import ru.fazziclay.schoolguide.util.AfterTextWatcher;
import ru.fazziclay.schoolguide.util.ColorUtil;

public class DebugActivity extends AppCompatActivity {
    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, DebugActivity.class);
    }

    private SchoolGuideApp app;
    private ActivityDebugBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = SchoolGuideApp.get(this);
        if (app == null) {
            setContentView(SharedConstrains.getAppNullView(this));
            return;
        }
        binding = ActivityDebugBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.debug_activityTitle);

        // ColorUtil.colorize
        binding.textColorizeInput.addTextChangedListener(new AfterTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                binding.textColorizeResult.setText(ColorUtil.colorize(s.toString(), Color.CYAN, Color.RED, Typeface.ITALIC));
            }
        });

        // Math.floor
        binding.mathFloorInput.addTextChangedListener(new AfterTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                try {
                    double a = Math.floor(Double.parseDouble(input));
                    binding.mathFloorOutput.setText(String.valueOf(a));
                } catch (Exception e) {
                    binding.mathFloorOutput.setText(e.toString());
                }
            }
        });

        // Debug callback signal
        binding.debugCallbackSignalSend.setOnClickListener(ignore -> {
            String data = binding.debugCallbackSignalData.getText().toString();
            app.getDebugSignalListenerCallbacks().run(((callbackStorage, callback) -> callback.run(data)));
        });

        // app.pendingUpdateGlobal
        binding.pendingUpdateGlobalSend.setOnClickListener(ignore -> app.pendingUpdateGlobal(binding.pendingUpdateGlobalStartupMode.isChecked()));

        // MilkLog
        binding.milkLogSend.setOnClickListener(ignore -> MilkLog.g(binding.milkLogText.getText().toString()));
    }
}