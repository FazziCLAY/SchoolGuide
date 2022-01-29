package ru.fazziclay.schoolguide.callback;

import java.util.ArrayList;
import java.util.List;

public class CallbackStorage <T extends ICallback> {
    private final List<T> callbacks = new ArrayList<>();

    public void run(RunCallbackInterface<T> runner) {
        int i = 0;
        while (i < callbacks.size()) {
            T callback = callbacks.get(i);

            if (callback == null) {
                i++;
                continue;
            }

            Status status = runner.run(this, callback);
            if (status.isDeleteCallback()) deleteCallback(callback);

            i++;
        }
    }

    public void deleteCallback(T callback) {
        callbacks.remove(callback);
    }

    public void addCallback(T callback) {
        callbacks.add(callback);
    }

    public interface RunCallbackInterface <T extends ICallback > {
        Status run(CallbackStorage<T> callbackStorage, T callback);
    }
}
