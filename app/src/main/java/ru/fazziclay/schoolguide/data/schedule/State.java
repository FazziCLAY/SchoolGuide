package ru.fazziclay.schoolguide.data.schedule;

import com.google.gson.annotations.SerializedName;

public enum State {
    @SerializedName("lesson_ending")
    LESSON_ENDING, // Урок в школе

    @SerializedName("lesson")
    LESSON, // Урок в школе

    @SerializedName("rest_ending")
    REST_ENDING, // Отдых оканчивается

    @SerializedName("rest")
    REST,   // Отдых,

    @SerializedName("end")
    END;     // Уроки окончены

    public boolean isLesson() {
        return this == LESSON || this == LESSON_ENDING;
    }

    public boolean isRest() {
        return this == REST || this == REST_ENDING;
    }

    public boolean isEnded() {
        return this == END;
    }

    public boolean isEnding() {
        return this == REST_ENDING || this == LESSON_ENDING;
    }
}
