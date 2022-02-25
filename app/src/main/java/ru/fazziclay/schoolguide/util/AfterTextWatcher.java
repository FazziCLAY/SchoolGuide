package ru.fazziclay.schoolguide.util;

import android.text.TextWatcher;

/**
 * Удаляет 2 ненужные функции, сделано для уменьшения размера TextWatcher'а в коде
 * **/
public abstract class AfterTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
}
