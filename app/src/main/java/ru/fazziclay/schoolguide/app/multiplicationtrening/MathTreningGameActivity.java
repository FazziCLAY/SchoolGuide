package ru.fazziclay.schoolguide.app.multiplicationtrening;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import java.security.SecureRandom;

import ru.fazziclay.schoolguide.R;
import ru.fazziclay.schoolguide.app.MilkLog;
import ru.fazziclay.schoolguide.databinding.ActivityMathTreningGameBinding;
import ru.fazziclay.schoolguide.util.DataUtil;
import ru.fazziclay.schoolguide.util.MathUtil;


public class MathTreningGameActivity extends AppCompatActivity {
    private static final String GAME_DATA_FILE = "math_trening_game.json";

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, MathTreningGameActivity.class);
    }
    // UI
    private ActivityMathTreningGameBinding binding;

    // Data
    private File gameDataFile;
    private MathTreningGameData gameData;

    private float number1;
    private float number2;
    private float result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMathTreningGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.mathTreningGame_activityTitle);

        // Load files
        gameDataFile = new File(getExternalFilesDir(null), GAME_DATA_FILE);
        gameData = DataUtil.load(gameDataFile, MathTreningGameData.class);
        gameData.getFirstNumberGenerator().fixIsAvailable();
        gameData.getLatestNumberGenerator().fixIsAvailable();
        if (gameData.getAction() == null) gameData.setAction("*");
        saveAll();

        clearInput();
        binding.resultInput.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                onKeyboardEnterKey();
                return true;
            }
            return false;
        });

        regenerateNumbers();
        updateTaskText();
        updateStatisticText();
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
        regenerateNumbers();
        updateTaskText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_math_trening_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.skip) {
            gameData.setScore(gameData.getScore() - 1);
            regenerateNumbers();
            updateTaskText();
            updateStatisticText();
            saveAll();

        } else if (item.getItemId() == R.id.gameSettings) {
            showSettingsDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog() {
        String[] actions = new String[]{"+", "-", "*", "/", "^"};
        int selected = 0;
        for (String s : actions) {
            if (s.equals(gameData.getAction())) break;
            selected++;
        }

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_math_trening_game_settings);

        Spinner spinner = dialog.findViewById(R.id.action);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, actions));
        spinner.setSelection(selected);

        EditText n1min = dialog.findViewById(R.id.n1min);
        n1min.setText(String.valueOf(gameData.getFirstNumberGenerator().getMinimum()));
        EditText n1max = dialog.findViewById(R.id.n1max);
        n1max.setText(String.valueOf(gameData.getFirstNumberGenerator().getMaximum()));
        EditText n2min = dialog.findViewById(R.id.n2min);
        n2min.setText(String.valueOf(gameData.getLatestNumberGenerator().getMinimum()));
        EditText n2max = dialog.findViewById(R.id.n2max);
        n2max.setText(String.valueOf(gameData.getLatestNumberGenerator().getMaximum()));

        Button cancel = dialog.findViewById(R.id.cancel);
        Button save = dialog.findViewById(R.id.save);

        Button resetScore = dialog.findViewById(R.id.resetScore);

        resetScore.setOnClickListener(ignore -> {
            dialog.cancel();
            showResetScoreDialog();
        });

        cancel.setOnClickListener(ignore -> dialog.cancel());

        save.setOnClickListener(ignore -> {
            gameData.setAction(actions[spinner.getSelectedItemPosition()]);
            try {
                gameData.getFirstNumberGenerator().setMinimum(Integer.parseInt(n1min.getText().toString()));
                gameData.getFirstNumberGenerator().setMaximum(Integer.parseInt(n1max.getText().toString()));
                gameData.getLatestNumberGenerator().setMinimum(Integer.parseInt(n2min.getText().toString()));
                gameData.getLatestNumberGenerator().setMaximum(Integer.parseInt(n2max.getText().toString()));

                gameData.getFirstNumberGenerator().fixIsAvailable();
                gameData.getLatestNumberGenerator().fixIsAvailable();
                saveAll();
            } catch (Exception e) {
                Toast.makeText(this, R.string.mathTreningGame_settings_exception_number, Toast.LENGTH_SHORT).show();
            }

            regenerateNumbers();
            updateTaskText();
            updateStatisticText();
            dialog.cancel();
        });

        dialog.show();
    }

    private void showResetScoreDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.mathTreningGame_resetScore_title)
                .setMessage(R.string.mathTreningGame_resetScore_message)
                .setPositiveButton(R.string.mathTreningGame_resetScore_reset, (dialog, which) -> {
                    boolean easterEgg = gameData.getScore() < 0;
                    gameData.setScore(0);
                    regenerateNumbers();
                    updateTaskText();
                    updateStatisticText();
                    dialog.cancel();
                    if (easterEgg) {
                        Toast.makeText(this, R.string.mathTreningGame_resetScore_easterEgg1, Toast.LENGTH_LONG).show();
                    }
                    saveAll();
                })
                .setNegativeButton(R.string.mathTreningGame_resetScore_cancel ,null)
                .show();
    }

    private void clearInput() {
        binding.resultInput.setText("");
    }

    private void onKeyboardEnterKey() {
        String userInput = binding.resultInput.getText().toString();
        if (userInput.isEmpty()) return;
        try {
            int userResult = Integer.parseInt(userInput);
            if (userResult == result) {
                regenerateNumbers();
                updateTaskText();
                gameData.setScore(gameData.getScore() + 1);
            } else {
                gameData.setScore(gameData.getScore() - 1);
            }
            updateStatisticText();
        } catch (Exception ignored) {}
        clearInput();
        saveAll();
    }

    private void regenerateNumbers() {
        SecureRandom random = new SecureRandom();
        number1 = MathUtil.random(random, gameData.getFirstNumberGenerator().getMinimum(), gameData.getFirstNumberGenerator().getMaximum());
        number2 = MathUtil.random(random, gameData.getLatestNumberGenerator().getMinimum(), gameData.getLatestNumberGenerator().getMaximum());
        result = calculate(gameData.getAction(), number1, number2);
    }

    private float calculate(String action, float n1, float n2) {
        switch (action) {
            case "*":
                return n1 * n2;
            case "-":
                return n1 - n2;
            case "/":
                return Math.round(n1 / n2);
            case "+":
                return n1 + n2;
            case "^":
                return Math.round(Math.pow(n1, n2));
        }
        MilkLog.g("WARN! MathTreningGameActivity.calculate action not supported!\nreturn 0.0f");
        return 0.0f;
    }

    private String createTaskText() {
        if ("^".equals(gameData.getAction())) {
            return String.format("%s^(%s)", Math.round(number1), Math.round(number2));
        }
        return String.format("%s %s %s", Math.round(number1), gameData.getAction(), Math.round(number2));
    }

    private void updateTaskText() {
        binding.taskText.setText(createTaskText());
    }

    private void updateStatisticText() {
        binding.statistic.setText(getString(R.string.mathTreningGame_statistic, String.valueOf(gameData.getScore())));
    }

    /**
     * Сохранить все данные
     * **/
    private void saveAll() {
        DataUtil.save(gameDataFile, gameData);
    }
}