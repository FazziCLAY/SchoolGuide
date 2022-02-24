package ru.fazziclay.schoolguide.datafixer.old.v50;

import com.google.gson.annotations.SerializedName;

public class V50MathTreningGameData {
    @SerializedName("score")
    public int score = 0;
    public String action;
    public GenRange firstNumberGenerator;
    public GenRange latestNumberGenerator;

    public static class GenRange {
        @SerializedName("minimum")
        public int minimum;

        @SerializedName("maximum")
        public int maximum;
    }
}
