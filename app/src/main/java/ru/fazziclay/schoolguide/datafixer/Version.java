package ru.fazziclay.schoolguide.datafixer;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Version {
    @SerializedName("firstVersion")
    int firstVersion = 0;

    @SerializedName("latestVersion")
    int latestVersion = 0;

    @SerializedName("versionsHistory")
    List<Integer> versionsHistory = new ArrayList<>();

    public Version(int firstVersion, int latestVersion, List<Integer> versionsHistory) {
        this.firstVersion = firstVersion;
        this.latestVersion = latestVersion;
        this.versionsHistory = versionsHistory;
    }

    public void addToVersionHistory(int version) {
        if (!versionsHistory.contains(version)) {
            versionsHistory.add(version);
        }
    }
}
