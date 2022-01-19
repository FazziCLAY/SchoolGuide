package ru.fazziclay.schoolguide.app.multiplicationtrening;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Random;

import ru.fazziclay.schoolguide.databinding.ActivityMultiplicationTreningBinding;


public class MultiplicationGameActivity extends AppCompatActivity {
    public static void open(Activity activity, boolean closeCurrent) {
        activity.startActivity(new Intent(activity, MultiplicationGameActivity.class));
        if (closeCurrent) activity.finish();
    }

    File gameDataFile;
    MultiplicationGameData gameData;

    ActivityMultiplicationTreningBinding binding;
    Random random = new Random();

    int n1;
    int n2;
    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiplicationTreningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("SchoolGuide - Multiplication");

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

        gameDataFile = new File(getExternalFilesDir(null), "multiplication_game.json");

        File old = new File(getExternalFilesDir(null), "multiplication_game_statistics.json");
        if (old.exists()) {
            old.renameTo(gameDataFile);
        }

        gameData = MultiplicationGameData.load(gameDataFile);
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
            gameData.action = "*";
            regenerate();
        }

        binding.taskText.setText(String.format("%s %s %s", n1, gameData.action, n2));
    }

    private void updateScore() {
        binding.score.setText(String.format("Score: %s", gameData.score));
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