package ru.fazziclay.schoolguide.app;

public class ManifestAutoUpdateCache {
    /**
     * Время последнего авто обновления в миллисекундах System.currentTimeMillis()
     * **/
    private long latestManifestAutoUpdated;

    public long getLatestManifestAutoUpdated() {
        return latestManifestAutoUpdated;
    }

    public void setLatestManifestAutoUpdated(long latestManifestAutoUpdated) {
        this.latestManifestAutoUpdated = latestManifestAutoUpdated;
    }
}
