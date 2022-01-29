package ru.fazziclay.schoolguide.app;

public class UpdateGlobalCache {
    /**
     * Время последнего авто обновления в миллисекундах System.currentTimeMillis()
     * **/
    private long latestGlobalUpdated;

    public long getLatestGlobalUpdated() {
        return latestGlobalUpdated;
    }

    public void setLatestGlobalUpdated(long latestGlobalUpdated) {
        this.latestGlobalUpdated = latestGlobalUpdated;
    }
}
