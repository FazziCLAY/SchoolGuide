package ru.fazziclay.schoolguide.callback;

public class Status {
    private final boolean deleteCallback;
    private final boolean isChangeImportance;
    private final CallbackImportance changeImportance;

    private Status(boolean deleteCallback, boolean isChangeImportance, CallbackImportance changeImportance) {
        this.deleteCallback = deleteCallback;
        this.isChangeImportance = isChangeImportance;
        this.changeImportance = changeImportance;
    }

    public boolean isDeleteCallback() {
        return deleteCallback;
    }

    public boolean isChangeImportance() {
        return isChangeImportance;
    }

    public CallbackImportance getChangeImportance() {
        return changeImportance;
    }

    public static class Builder {
        private boolean deleteCallback;
        private boolean isChangeImportance;
        private CallbackImportance changeImportance;

        public Builder setDeleteCallback(boolean deleteCallback) {
            this.deleteCallback = deleteCallback;
            return this;
        }

        public Builder setNewImportance(CallbackImportance importance) {
            this.isChangeImportance = importance != null;
            this.changeImportance = importance;
            return this;
        }

        public Status build() {
            return new Status(deleteCallback, isChangeImportance, changeImportance);
        }
    }
}
