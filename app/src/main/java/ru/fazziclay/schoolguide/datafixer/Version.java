package ru.fazziclay.schoolguide.datafixer;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Version {
    @SerializedName("firstVersion")
    private int firstVersion = 0;

    @SerializedName("latestVersion")
    private int latestVersion = 0;

    @SerializedName("versionsHistory")
    private List<Integer> versionsHistory = new ArrayList<>();

    public Version(int firstVersion, int latestVersion, List<Integer> versionsHistory) {
        this.firstVersion = firstVersion;
        this.latestVersion = latestVersion;
        this.versionsHistory = versionsHistory;
    }

    public void addToVersionHistory(int version) {
        if (versionsHistory == null) versionsHistory = new ArrayList<>();
        if (!versionsHistory.contains(version)) {
            versionsHistory.add(version);
        }
    }

    public int getFirstVersion() {
        return firstVersion;
    }

    public void setFirstVersion(int firstVersion) {
        this.firstVersion = firstVersion;
    }

    public int getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(int latestVersion) {
        this.latestVersion = latestVersion;
    }
}
