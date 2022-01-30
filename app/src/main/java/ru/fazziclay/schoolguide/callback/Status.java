package ru.fazziclay.schoolguide.callback;

public class Status {
    private final boolean deleteCallback;
    private final boolean changeImportance;
    private final CallbackImportance changeImportanceTo;

    private Status(boolean deleteCallback, boolean changeImportance, CallbackImportance changeImportanceTo) {
        this.deleteCallback = deleteCallback;
        this.changeImportance = changeImportance;
        this.changeImportanceTo = changeImportanceTo;
    }

    public boolean isDeleteCallback() {
        return deleteCallback;
    }

    public boolean isChangeImportance() {
        return changeImportance;
    }

    public CallbackImportance getChangeImportanceTo() {
        return changeImportanceTo;
    }

    public static class Builder {
        private boolean deleteCallback;
        private boolean changeImportance;
        private CallbackImportance changeTo;

        public Builder setDeleteCallback(boolean deleteCallback) {
            this.deleteCallback = deleteCallback;
            return this;
        }

        public Builder setChangeImportance(CallbackImportance importance) {
            this.changeImportance = true;
            this.changeTo = importance;
            return this;
        }

        public Status build() {
            return new Status(deleteCallback, changeImportance, changeTo);
        }
    }
}
