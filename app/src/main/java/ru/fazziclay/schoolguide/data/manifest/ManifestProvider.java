package ru.fazziclay.schoolguide.data.manifest;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.util.FileUtil;
import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.BaseProvider;

public class ManifestProvider extends BaseProvider {
    private static final String MANIFEST_LOCAL_FILE = "manifest.json";
    private static final String MANIFEST_URL = "https://github.com/fazziclay/schoolguide/manifest.json";
    private static final String MANIFEST_KEY_URL = "https://github.com/fazziclay/schoolguide/manifest.json.key";

    private static final int CURRENT_FORMAT_VERSION = 2;

    boolean isUnstable = false;

    public ManifestProvider(Context context) {
        filePath = context.getExternalFilesDir(null).getAbsolutePath().concat("/").concat(MANIFEST_LOCAL_FILE);
        data = load();

        if (data.isFormatVersionDefault()) data.formatVersion = CURRENT_FORMAT_VERSION;
        if (data.formatVersion != CURRENT_FORMAT_VERSION) {
            isUnstable = true;
        }

        new Thread(() -> updateForGlobal((exception, manifestProvider) -> {})).start();

        save();
    }

    @Override
    public BaseData load() {
        Gson gson = new Gson();
        return gson.fromJson(FileUtil.read(filePath, "{}"), Manifest.class);
    }

    public void updateForGlobal(UpdateForGlobalInterface updateForGlobalInterface) {
        Gson gson = new Gson();
        int key;
        Exception exception = null;

        try {
            key = Integer.parseInt(parseUrl(MANIFEST_KEY_URL));

            if (getManifest().manifestKey != key) {
                Manifest globalManifest = gson.fromJson(parseUrl(MANIFEST_URL), Manifest.class);
                setManifest(globalManifest);
            }
        } catch (Exception e) {
            exception = e;
        }
        updateForGlobalInterface.run(exception, this);
    }

    public void setManifest(Manifest manifest) {
        data = manifest;
    }

    public Manifest getManifest() {
        return (Manifest) data;
    }

    public AppVersion getLatestAppVersion() {
        return getManifest().latestVersion;
    }

    public VersionState getAppVersionState() {
        AppVersion latestVersion = getLatestAppVersion();
        AppVersion appVersion = SharedConstrains.APP_VERSION;

        if (latestVersion == null) {
            return VersionState.UNKNOWN;
        }

        if (appVersion.code == latestVersion.code) return VersionState.LATEST;
        if (appVersion.code < latestVersion.code) return VersionState.OUTDATED;
        return VersionState.UNKNOWN;
    }

    // ===================================
    // P  R  I  V  A  T  E      Z  O  N  E
    // ===================================
    private String parseUrl(String urlINPUT) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlINPUT);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        bufferedReader.close();
        return result.toString();
    }
}
