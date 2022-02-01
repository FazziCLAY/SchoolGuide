package ru.fazziclay.schoolguide.app.global;

import java.util.HashMap;

public class GlobalVersionManifest implements GlobalData {
    public int key = 0;
    public ManifestVersion latestVersion;

    @Override
    public int getGlobalKey() {
        return key;
    }

    public static class ManifestVersion {
        public int code = 0;
        public String name = null;
        public HashMap<String, String> changelog = new HashMap<>();
        public HashMap<String, String> download = new HashMap<>();


        public ManifestVersion(int code, String name, HashMap<String, String> changelog, HashMap<String, String> download) {
            this.code = code;
            this.name = name;
            this.changelog = changelog;
            this.download = download;
        }

        public String getDownloadUrl(String type) {
            if (download == null) return null;
            final String release = "release";
            final String debug = "debug";
            final String _type = type.equals("debug") ? debug : release;
            if (!download.containsKey(_type)) {
                return null;
            }
            return download.get(_type);
        }

        public String getChangelog(String language) {
            if (changelog == null) return null;
            if (changelog.containsKey(language)) {
                return changelog.get(language);
            }
            if (changelog.containsKey("default")) {
                return changelog.get("default");
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }
    }
}
