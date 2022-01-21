package ru.fazziclay.schoolguide.app.multiplicationtrening;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.databinding.ActivityMultiplicationTreningBinding;


public class MathTreningGameActivity extends AppCompatActivity {
    public static void open(Activity activity) {
        activity.startActivity(new Intent(activity, MathTreningGameActivity.class));
    }

    File gameDataFile;
    MathTreningGameData gameData;

    ActivityMultiplicationTreningBinding binding;
    Random random = new Random();


    private static final int M = 15;
    double speed = 0;

    int speedI = 0;
    long[] starts = new long[M];
    long[] durations = new long[M];

    int n1;
    int n2;
    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiplicationTreningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("SchoolGuide - Math trening"); // TODO: 2022-01-21 make translatable

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
                String text = binding.resultInput.getText().toString();
                int number = toInt(text);
                if (text.contains(".") || text.contains("-")) {
                    text = text
                            .replace(".", "")
                            .replace("-", "");
                    binding.resultInput.setText(text);
                    binding.resultInput.setSelection(cursor-1);
                }
                if (!text.equals(String.valueOf(number)) && number != Integer.MAX_VALUE) {
                    binding.resultInput.setText(String.valueOf(number));
                    binding.resultInput.setSelection(cursor - 1);
                }
            }
        });

        gameDataFile = new File(getExternalFilesDir(null), "math_trening_game.json");

        File old = new File(getExternalFilesDir(null), "multiplication_game_statistics.json");
        if (old.exists()) {
            old.renameTo(gameDataFile);
        }

        // TODO: 2022-01-21 delete non used data fixer

        old = new File(getExternalFilesDir(null), "multiplication_game.json");
        if (old.exists()) {
            old.renameTo(gameDataFile);
        }

        gameData = MathTreningGameData.load(gameDataFile);
        saveAll();

        clearInput();
        updateScore();
        regenerate();
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
        updateScore();
        regenerate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_math_trening_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.resetAllProgressItem) {
            gameData.score = 0;
            saveAll();

            open(this);
            finish();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
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
            durations[speedI] = System.currentTimeMillis() - starts[speedI];

            long sum = 0;
            int len = 0;
            for (long dur : durations) {
                if (dur == 0) continue;
                sum += dur;
                len++;
            }
            speed = 1000 / (((double) sum) / ((double)len));

            gameData.score++;
            regenerate();
        } else if (userResult != Integer.MAX_VALUE) {
            gameData.score--;
        }
        updateScore();
        saveAll();
    }

    private void regenerate() {
        int on1 = n1, on2 = n2;

        n1 = random(1);
        n2 = random(2);

        if ((on1 == n1 && on2 == n2)) {
            random = new Random();
            n1 = random(1);
            n2 = random(2);
        }

        if ("*".equals(gameData.action)) {
            result = n1 * n2;
        } else if ("-".equals(gameData.action)) {
            result = n1 - n2;
        } else if ("/".equals(gameData.action)) {
            result = n1 / n2;
        } else if ("+".equals(gameData.action)) {
            result = n1 + n2;
        } else {
            Toast.makeText(this, "Unknown action: " + gameData.action, Toast.LENGTH_SHORT).show();
            // TODO: 2022-01-21 make translatable
            gameData.action = "*";
            regenerate();
        }

        binding.taskText.setText(String.format("%s %s %s", n1, gameData.action, n2));
        speedI++;
        if (speedI >= M) speedI = 0;

        starts[speedI] = System.currentTimeMillis();
    }

    private void updateScore() {
        binding.score.setText(String.format("Score: %s; Speed: %s", gameData.score, round(speed, 2)));
        // TODO: 2022-01-21 make translatable
    }

    public double round(double d, int numbers) {
        double f = Math.pow(10, numbers);
        double i = d * f;
        return Math.round(i) / f;
    }

    private int random(int i) {
        int rangeMax = 0, rangeMin = 0;
        if (i == 1) {
            rangeMax = gameData.n1RangeMax;
            rangeMin = gameData.n1RangeMin;
        } else {
            rangeMax = gameData.n2RangeMax;
            rangeMin = gameData.n2RangeMin;
        }
        return random.nextInt(rangeMax + 1 - rangeMin) + rangeMin;
    }

    private void saveAll() {
        gameData.save(gameDataFile);
    }
}