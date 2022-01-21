package ru.fazziclay.schoolguide.app.multiplicationtrening;

import java.io.File;

import ru.fazziclay.schoolguide.util.DataUtil;

public class MathTreningGameData {
    public static MathTreningGameData load(File file) {
        return (MathTreningGameData) DataUtil.load(file, MathTreningGameData.class);
    }

    public void save(File file) {
        DataUtil.save(file, this);
    }

    public int score = 0;
    public String action = "*";
    int n1RangeMin = 2;
    int n1RangeMax = 9;

    int n2RangeMin = 2;
    int n2RangeMax = 9;
}
