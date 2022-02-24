package ru.fazziclay.schoolguide.app.global;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * <h1>Глобальный Манифест версий</h1>
 * <p>Хранит последнюю версию приложения и нужные для скачивания и идентификации данные</p>
 * @see GlobalManager
 * **/
public class GlobalVersionManifest implements IGlobalData {
    /**
     * <h1>Global key</h1>
     * Name "key" since pre v50
     * @see IGlobalData
     * **/
    @SerializedName("key")
    private int key = 0;

    /**
     * <h1>Последняя версия приложения</h1>
     * Name "latestVersion" since pre v50
     * **/
    @SerializedName("latestVersion")
    private ManifestVersion latestVersion = null;

    @Override
    public int getGlobalKey() {
        return key;
    }

    public ManifestVersion getLatestVersion() {
        return latestVersion;
    }

    /**
     * Манифест-версия
     * names "code", "name", "changelog", "download"("debug", "release") since pre v50
     * **/
    public static class ManifestVersion {
        @SerializedName("code")
        private final int code;

        @SerializedName("name")
        private final String name;

        @SerializedName("changelog")
        private final HashMap<String, String> changelog;

        @SerializedName("download")
        private final HashMap<String, String> download;

        public ManifestVersion(int code, String name, HashMap<String, String> changelog, HashMap<String, String> download) {
            this.code = code;
            this.name = name;
            this.changelog = changelog;
            this.download = download;
        }

        /**
         * Код версии
         * **/
        public int getCode() {
            return code;
        }

        /**
         * Название версии
         * **/
        public String getName() {
            return name;
        }

        /**
         * Выдать ссылку для загрузки, есть передать "debug" то искать ссылку по ключу "debug"
         * иначе искать ссылку по ключу "release"
         * если ссылки нету выдаём null
         * **/
        public String getDownloadUrl(String type) {
            if (download == null) return null;
            final String KEY_RELEASE = "release";
            final String KEY_DEBUG = "debug";
            if (type == null) type = KEY_RELEASE;
            final String key = type.equals("debug") ? KEY_DEBUG : KEY_RELEASE;
            if (!download.containsKey(key)) {
                return null;
            }
            return download.get(key);
        }

        /**
         * Выдём список изменений по ключу языка
         * Если такого ключа в списке нету выдаём по ключу "default"
         * Если ключа "default" нету выдаём null
         * **/
        public String getChangelog(String language) {
            if (changelog == null) return null;
            if (language == null) language = "default";
            if (changelog.containsKey(language)) {
                return changelog.get(language);
            }
            if (changelog.containsKey("default")) {
                return changelog.get("default");
            }
            return null;
        }
    }
}
