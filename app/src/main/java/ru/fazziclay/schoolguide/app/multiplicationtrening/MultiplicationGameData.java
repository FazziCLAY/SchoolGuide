package ru.fazziclay.schoolguide.app.multiplicationtrening;

import java.io.File;

import ru.fazziclay.schoolguide.util.data.BaseData;

public class MultiplicationGameData extends BaseData {
    public static MultiplicationGameData load(File file) {
        return (MultiplicationGameData) load(file, MultiplicationGameData.class);
    }

    public int score = 0;
    public String action = "*";
    int rangeMin = 2;
    int rangeMax = 9;
}
