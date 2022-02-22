package ru.fazziclay.schoolguide.callback;

/**
 * Важность Callback'а чем queuePosition выше тем раньше выполняется Callback
 * <p>{@link CallbackImportance#MAX} - q=100</p>
 * <p>{@link CallbackImportance#HIGH} - q=50</p>
 * <p>{@link CallbackImportance#DEFAULT} - q=0</p>
 * <p>{@link CallbackImportance#LOW} - q=-50</p>
 * <p>{@link CallbackImportance#MIN} - q=-100</p>
 *
 * По умолчанию лучше использовать DEFAULT
 * @see CallbackStorage
 * **/
public enum CallbackImportance {
    MAX(100),
    HIGH(50),
    DEFAULT(0),
    LOW(-50),
    MIN(-100);

    private final int queuePosition;

    CallbackImportance(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public int getQueuePosition() {
        return queuePosition;
    }
}
