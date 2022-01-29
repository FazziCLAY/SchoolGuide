package ru.fazziclay.schoolguide.callback;

public class Status {
    private boolean deleteCallback = false;

    private Status(boolean deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    public boolean isDeleteCallback() {
        return deleteCallback;
    }

    public static class Builder {
        boolean deleteCallback;

        public Builder setDeleteCallback(boolean deleteCallback) {
            this.deleteCallback = deleteCallback;
            return this;
        }

        public Status build() {
            return new Status(deleteCallback);
        }
    }
}
