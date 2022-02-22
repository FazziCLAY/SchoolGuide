package ru.fazziclay.schoolguide.callback;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранилище Callback'ов
 * **/
public class CallbackStorage <T extends Callback> {
    private final List<CallbackInternal> callbacks = new ArrayList<>();

    public void run(RunCallbackInterface<T> runner) {
        int importanceI = 0;
        final CallbackImportance[] importances = CallbackImportance.values();
        while (importanceI < importances.length) {
            CallbackImportance useCallbackImportance = importances[importanceI];

            run(runner, useCallbackImportance.getQueuePosition());

            importanceI++;
        }
    }

    private void run(RunCallbackInterface<T> runner, int importance) {
        int i = 0;
        while (i < callbacks.size()) {
            CallbackInternal internal = callbacks.get(i);
            if (importance != internal.importance.getQueuePosition()) {
                i++;
                continue;
            }

            Status status = runner.run(this, internal.callback);
            if (status != null) {
                if (status.isDeleteCallback()) deleteCallback(internal);
                if (status.isChangeImportance()) internal.importance = status.getNewImportance();
            }
            i++;
        }
    }

    public void deleteCallback(CallbackInternal callbackInternal) {
        callbacks.remove(callbackInternal);
    }

    public void deleteCallback(T callback) {
        int i = 0;
        while (i < callbacks.size()) {
            CallbackInternal internal = callbacks.get(i);
            if (internal.callback == callback) callbacks.remove(internal);
            i++;
        }
    }

    public void addCallback(CallbackImportance importance, T callback) {
        callbacks.add(new CallbackInternal(callback, importance));
    }

    public void changeImportance(T callback, CallbackImportance importance) {
        int i = 0;
        while (i < callbacks.size()) {
            CallbackInternal internal = callbacks.get(i);
            if (internal.callback == callback) internal.importance = importance;
            i++;
        }
    }

    public interface RunCallbackInterface <T extends Callback> {
        Status run(CallbackStorage<T> callbackStorage, T callback);
    }

    private class CallbackInternal {
        T callback;
        CallbackImportance importance;

        public CallbackInternal(T callback, CallbackImportance importance) {
            this.callback = callback;
            this.importance = importance;
        }
    }
}
