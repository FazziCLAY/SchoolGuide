package ru.fazziclay.schoolguide.app.multiplicationtrening;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Random;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.databinding.ActivityMathTreningGameBinding;
import ru.fazziclay.schoolguide.util.DataUtil;


public class MathTreningGameActivity extends AppCompatActivity {
    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, MathTreningGameActivity.class);
    }

    private File gameDataFile;
    private MathTreningGameData gameData;

    ActivityMathTreningGameBinding binding;
    Random random = new Random();

    private static final int SPEED_ITEMS = 15;

    private double maxSpeed = 0;
    private double averageDuration = 0;
    private int i = 0;
    private long start;
    private final long[] durations = new long[SPEED_ITEMS];

    private float n1;
    private float n2;
    private float result;

    Handler timeUpdateHandler;
    Runnable timeUpdateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMathTreningGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.mathTreningGame_activityTitle);

        binding.resultInput.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                onEnter();
                return true;
            }
            return false;
        });

        binding.resultInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int cursor = binding.resultInput.getSelectionStart();
                if (cursor < 0) return;
                String text = binding.resultInput.getText().toString();
                int number = toInt(text);
                if (!text.equals("-") && !text.equals(String.valueOf(number)) && number != Integer.MAX_VALUE) {
                    binding.resultInput.setText(String.valueOf(number));
                    binding.resultInput.setSelection(cursor - 1);
                }
            }
        });

        gameDataFile = new File(getExternalFilesDir(null), "math_trening_game.json");
        gameData = DataUtil.load(gameDataFile, MathTreningGameData.class);
        fixGenerationRange();
        saveAll();

        start = System.currentTimeMillis();

        clearInput();
        updateStatisticText();
        regenerate();

        timeUpdateHandler = new Handler(getMainLooper());
        timeUpdateRunnable = () -> {
            updateStatisticText();
            timeUpdateHandler.postDelayed(timeUpdateRunnable, 20);
        };
        timeUpdateHandler.post(timeUpdateRunnable);
    }

    private void fixGenerationRange() {
        if (gameData.n1RangeMin > gameData.n1RangeMax) {
            int min = gameData.n1RangeMin;
            gameData.n1RangeMin = gameData.n1RangeMax;
            gameData.n1RangeMax = min;
        }
        if (gameData.n2RangeMin > gameData.n2RangeMax) {
            int min = gameData.n2RangeMin;
            gameData.n2RangeMin = gameData.n2RangeMax;
            gameData.n2RangeMax = min;
        }
    }

    private void nextDuration() {
        i++;
        if (i > SPEED_ITEMS-1) {
            i = 0;
        }
        start = System.currentTimeMillis();
    }

    private void updateDurations() {
        if (start > 0) {
            durations[i] = System.currentTimeMillis() - start;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveAll();
    }

    @Override
    protected void onResume() {
        super.onResume();

        clearInput();
        regenerate();
        updateStatisticText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_math_trening_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.skip) {
            gameData.score -= 1;
            saveAll();
            regenerate();

        } else if (item.getItemId() == R.id.gameSettings) {
            showSettingsDialog();

        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showSettingsDialog() {
        String[] actions = new String[]{"+", "-", "*", "/", "^"};
        int selected = 0;
        for (String s : actions) {
            if (gameData.action.equals(s)) break;
            selected++;
        }

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_math_trening_game_settings);

        Spinner spinner = dialog.findViewById(R.id.action);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, actions));
        spinner.setSelection(selected);

        EditText n1min = dialog.findViewById(R.id.n1min);
        n1min.setText(String.valueOf(gameData.n1RangeMin));
        EditText n1max = dialog.findViewById(R.id.n1max);
        n1max.setText(String.valueOf(gameData.n1RangeMax));
        EditText n2min = dialog.findViewById(R.id.n2min);
        n2min.setText(String.valueOf(gameData.n2RangeMin));
        EditText n2max = dialog.findViewById(R.id.n2max);
        n2max.setText(String.valueOf(gameData.n2RangeMax));

        Button cancel = dialog.findViewById(R.id.cancel);
        Button save = dialog.findViewById(R.id.save);

        Button resetScore = dialog.findViewById(R.id.resetScore);

        resetScore.setOnClickListener(ignore -> {
            dialog.cancel();
            showResetScoreDialog();
        });

        cancel.setOnClickListener(ignore -> {
            start = System.currentTimeMillis();
            dialog.cancel();
        });

        save.setOnClickListener(ignore -> {
            String newAction = actions[spinner.getSelectedItemPosition()];
            int newN1min = toInt(n1min.getText().toString());
            int newN1max = toInt(n1max.getText().toString());
            int newN2min = toInt(n2min.getText().toString());
            int newN2max = toInt(n2max.getText().toString());
            if (newN1max == Integer.MAX_VALUE || newN2max == Integer.MAX_VALUE || newN1min == Integer.MAX_VALUE || newN2min == Integer.MAX_VALUE) {
                Toast.makeText(this, "Number error!", Toast.LENGTH_SHORT).show();
                return;
            }

            gameData.action = newAction;
            gameData.n1RangeMin = newN1min;
            gameData.n1RangeMax = newN1max;
            gameData.n2RangeMin = newN2min;
            gameData.n2RangeMax = newN2max;
            fixGenerationRange();
            saveAll();

            regenerate();
            dialog.cancel();
        });

        dialog.show();
    }

    private void showResetScoreDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.mathTreningGame_resetScore_title)
                .setMessage(R.string.mathTreningGame_resetScore_message)
                .setPositiveButton(R.string.mathTreningGame_resetScore_reset, (dialog, which) -> {
                    gameData.score = 0;
                    saveAll();
                    dialog.cancel();
                    startActivity(getLaunchIntent(this));
                    finish();
                })
                .setNegativeButton(R.string.mathTreningGame_resetScore_cancel ,null)
                .show();
    }

    private void clearInput() {
        binding.resultInput.setText("");
    }

    private int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignored) {
            return Integer.MAX_VALUE;
        }
    }

    private void onEnter() {
        String userInput = binding.resultInput.getText().toString();
        if (userInput.isEmpty()) return;
        int userResult = toInt(userInput);
        clearInput();
        if (userResult == result) {
            regenerate();
            gameData.score++;
            nextDuration();

        } else if (userResult != Integer.MAX_VALUE) {
            gameData.score--;
        }
        updateStatisticText();
        saveAll();
    }

    private void regenerate() {
        float oldN1 = n1, oldN2 = n2;

        n1 = random(1);
        n2 = random(2);

        if ((oldN1 == n1 && oldN2 == n2)) {
            random = new Random();
            n1 = random(1);
            n2 = random(2);
        }

        result = calculate(gameData.action, n1, n2);
        binding.taskText.setText(createTaskText());
    }

    private float calculate(String action, float n1, float n2) {
        if ("*".equals(action)) {
            return n1 * n2;
        } else if ("-".equals(action)) {
            return n1 - n2;
        } else if ("/".equals(action)) {
            return Math.round(n1 / n2);
        } else if ("+".equals(action)) {
            return n1 + n2;
        } else if ("^".equals(action)) {
            return (float) Math.pow(n1, n2);
        }
        return 0.0f;
    }

    private String createTaskText() {
        if ("^".equals(gameData.action)) {
            return String.format("%s^(%s)", Math.round(n1), Math.round(n2));
        }
        return String.format("%s %s %s", Math.round(n1), gameData.action, Math.round(n2));
    }

    private double average(long[] d) {
        long sum = 0;
        int len = 0;
        for (long dd : d) {
            if (dd == 0) continue;
            sum += dd;
            len++;
        }

        double a = ((double) sum) / ((double)len);
        return sum == 0 ? 0 : a;
    }

    private void updateStatisticText() {
        updateDurations();
        averageDuration = average(durations);
        double speed = 1000 / averageDuration;
        if (speed > maxSpeed) {
            maxSpeed = 1000 / averageDuration;
        }

        binding.statistic.setText(getString(R.string.mathTreningGame_statistic, String.valueOf(gameData.score)));
    }

    public static double round(double d, int numbers) {
        double f = Math.pow(10, numbers);
        return Math.round(d * f) / f;
    }

    private int random(int position) {
        int rangeMax = gameData.n1RangeMax, rangeMin = gameData.n1RangeMin;
        if (position == 2)  {
            rangeMax = gameData.n2RangeMax;
            rangeMin = gameData.n2RangeMin;
        }
        return random.nextInt(rangeMax + 1 - rangeMin) + rangeMin;
    }

    private void saveAll() {
        DataUtil.save(gameDataFile, gameData);
    }
}