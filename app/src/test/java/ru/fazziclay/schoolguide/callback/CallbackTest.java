package ru.fazziclay.schoolguide.callback;

import org.junit.Test;

public class CallbackTest {
    @Test
    public void test() {
        CallbackStorage<TestCallback> storage = new CallbackStorage<>();

        storage.addCallback(CallbackImportance.DEFAULT, message -> System.out.println("DEFAULT Message from callback: "+message));

        storage.addCallback(CallbackImportance.MAX, message -> System.out.println("MAX Message from callback: "+message));

        storage.addCallback(CallbackImportance.MIN, message -> System.out.println("MIN Message from callback: "+message));

        storage.addCallback(CallbackImportance.HIGH, message -> System.out.println("HIGH Message from callback: "+message));

        storage.run((callbackStorage, callback) -> {
            callback.sendMessage("State 1(разные приоритеты)");

            return new Status.Builder()
                    .setDeleteCallback(false)
                    .setNewImportance(CallbackImportance.MAX)
                    .build();
        });

        storage.run((callbackStorage, callback) -> {
            callback.sendMessage("State 2(state 1 должен был сделать везде приоритет MAX)");

            return new Status.Builder()
                    .build();
        });
    }

    private interface TestCallback extends Callback {
        void sendMessage(String message);
    }
}
