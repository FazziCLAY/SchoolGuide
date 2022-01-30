package ru.fazziclay.schoolguide.callback;

import java.util.ArrayList;
import java.util.List;

public class CallbackStorage <T extends ICallback> {
    private final List<CallbackInternal> callbacks = new ArrayList<>();

    public void run(RunCallbackInterface<T> runner) {
        int importanceI = 0;
        CallbackImportance[] importances = CallbackImportance.values();
        while (importanceI < importances.length) {
            CallbackImportance useCallbackImportance = importances[importanceI];

            run(runner, useCallbackImportance.getI());

            importanceI++;
        }
    }

    private void run(RunCallbackInterface<T> runner, int importance) {
        int i = 0;
        while (i < callbacks.size()) {
            CallbackInternal internal = callbacks.get(i);
            if (importance != internal.importance.getI()) {
                i++;
                continue;
            }

            Status status = runner.run(this, internal.callback);
            if (status.isDeleteCallback()) deleteCallback(internal);
            if (status.isChangeImportance()) internal.importance = status.getChangeImportanceTo();

            i++;
        }
    }

    public void deleteCallback(CallbackInternal callbackInternal) {
        callbacks.remove(callbackInternal);
    }

    public void addCallback(CallbackImportance importance, T callback) {
        callbacks.add(new CallbackInternal(callback, importance));
    }


    public interface RunCallbackInterface <T extends ICallback > {
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
