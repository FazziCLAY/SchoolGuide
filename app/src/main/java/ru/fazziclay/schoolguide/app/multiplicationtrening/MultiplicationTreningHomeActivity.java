package ru.fazziclay.schoolguide.app.multiplicationtrening;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import ru.fazziclay.schoolguide.databinding.ActivityMultiplicationTreningHomeBinding;


public class MultiplicationTreningHomeActivity extends AppCompatActivity {
    ActivityMultiplicationTreningHomeBinding binding;
    int i1;
    int i2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiplicationTreningHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.resultInput.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                String userInput = binding.resultInput.getText().toString();
                binding.resultInput.setText("");
                if (userInput.isEmpty() || userInput.contains(".")) return true;
                int userInt = 0;
                try {
                    userInt = Integer.parseInt(userInput);
                    enter(userInt);
                } catch (Exception ignored) {}
                return true;
            }

            return false;
        });

        regenerate();
    }

    void enter(int number) {
        if (i1 * i2 == number) {
            regenerate();
        }
    }

    void regenerate() {
        Random random = new Random();
        i1 = random(random, 2, 9);
        i2 = random(random, 2, 9);

        binding.taskText.setText(String.format("%s * %s", i1, i2));
    }

    int random(Random random, int min, int max) {
        return random.nextInt(max - min) + min;
    }
}