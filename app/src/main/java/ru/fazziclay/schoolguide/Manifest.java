package ru.fazziclay.schoolguide;

import java.util.HashMap;
import java.util.Locale;

public class Manifest {
    public LatestVersionStorage latest = new LatestVersionStorage();

    public static class LatestVersionStorage {
        public LatestVersion release = new LatestVersion();
        public LatestVersion debug = new LatestVersion();
    }

    public static class LatestVersion {
        public int code = 0;
        public String name = "Unknown";
        HashMap<String, String> changelog = new HashMap<>();
        HashMap<String, String> download = new HashMap<>();

        public String getDownloadUrl() {
            return download.get(SharedConstrains.APPLICATION_BUILD_TYPE.equals("debug") ? "debug" : "release");
        }

        public String getChangelog() {
            String locale = Locale.getDefault().getLanguage();
            if (changelog.containsKey(locale)) {
                return changelog.get(locale);
            }
            return changelog.get("default");
        }
    }
}
