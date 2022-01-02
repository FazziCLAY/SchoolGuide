package ru.fazziclay.schoolguide.data.schedule;

import com.google.gson.annotations.SerializedName;

public enum State {
    @SerializedName("lesson")
    LESSON, // Урок

    @SerializedName("rest")
    REST,   // Отдых,

    @SerializedName("end")
    END;    // Уроки окончены

    boolean isEnding = false;

    public State setEnding(boolean ending) {
        isEnding = ending;
        return this;
    }

    public boolean isEnding() {
        return isEnding;
    }

    public boolean isLesson() {
        return this == LESSON;
    }

    public boolean isRest() {
        return this == REST;
    }

    public boolean isEnded() {
        return this == END;
    }
}
