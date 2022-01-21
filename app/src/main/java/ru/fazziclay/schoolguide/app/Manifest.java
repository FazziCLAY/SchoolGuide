package ru.fazziclay.schoolguide.app;

import java.util.HashMap;
import java.util.Locale;

import ru.fazziclay.schoolguide.SharedConstrains;

public class Manifest {
    public ManifestVersions latest = new ManifestVersions();

    public static class ManifestVersions {
        public ManifestVersion release = new ManifestVersion();
    }

    public static class ManifestVersion {
        int code = 0;
        String name = "Unknown";
        HashMap<String, String> changelog = new HashMap<>();
        HashMap<String, String> download = new HashMap<>();


        public ManifestVersion(int code, String name, HashMap<String, String> changelog, HashMap<String, String> download) {
            this.code = code;
            this.name = name;
            this.changelog = changelog;
            this.download = download;
        }

        public ManifestVersion() {}

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

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }
    }
}
