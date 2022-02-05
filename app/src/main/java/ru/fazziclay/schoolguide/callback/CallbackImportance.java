package ru.fazziclay.schoolguide.callback;

public enum CallbackImportance {
    MAX(100),
    HIGH(50),
    DEFAULT(0),
    LOW(-50),
    MIN(-100);

    private int i = 0;
    CallbackImportance(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }
}
