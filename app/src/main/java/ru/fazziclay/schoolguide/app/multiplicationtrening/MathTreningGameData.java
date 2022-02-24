package ru.fazziclay.schoolguide.app.multiplicationtrening;

import com.google.gson.annotations.SerializedName;

/**
 * @since v50
 * **/
public class MathTreningGameData {
    @SerializedName("score")
    private int score = 0;
    private String action = "*";
    private final GenRange firstNumberGenerator = new GenRange(2, 10);
    private final GenRange latestNumberGenerator = new GenRange(2, 10);

    public int getScore() {
        return score;
    }

    public String getAction() {
        return action;
    }

    public GenRange getFirstNumberGenerator() {
        return firstNumberGenerator;
    }

    public GenRange getLatestNumberGenerator() {
        return latestNumberGenerator;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public static class GenRange {
        /**
         * @since v50
         * **/
        @SerializedName("minimum")
        private int minimum;

        /**
         * @since v50
         * **/
        @SerializedName("maximum")
        private int maximum;

        public GenRange(int minimum, int maximum) {
            this.minimum = minimum;
            this.maximum = maximum;
        }

        public boolean isMinMoreMax() {
            return minimum > maximum;
        }

        public void fixIsAvailable() {
            if (isMinMoreMax()) flip();
        }

        public void flip() {
            if (minimum > maximum) {
                int tempMin = minimum;
                minimum = maximum;
                maximum = tempMin;
            }
        }

        public int getMinimum() {
            return minimum;
        }

        public void setMinimum(int minimum) {
            this.minimum = minimum;
        }

        public int getMaximum() {
            return maximum;
        }

        public void setMaximum(int maximum) {
            this.maximum = maximum;
        }
    }
}
