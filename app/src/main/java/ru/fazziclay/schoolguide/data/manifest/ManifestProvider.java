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

    private static final int CURRENT_FORMAT_VERSION = 4;

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
        Manifest manifest = gson.fromJson(FileUtil.read(filePath, "{}"), Manifest.class);
        manifest.appVersion = SharedConstrains.APP_VERSION;
        return manifest;
    }

    public void updateForGlobal(UpdateForGlobalInterface updateForGlobalInterface) {
        // DEV
        {
            try {
                Thread.sleep(SharedConstrains.DEV_FEATURED_MANIFEST_GLOBAL_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (SharedConstrains.DEV_FEATURED_MANIFEST_ONLY_FILE) {
                updateForGlobalInterface.run(null, this);
                return;
            }
        }
        // Start code from this

        Gson gson = new Gson();
        int key;
        Exception exception = null;

        try {
            key = Integer.parseInt(parseUrl(MANIFEST_KEY_URL));

            if (getManifest().manifestKey != key) {
                Manifest globalManifest = gson.fromJson(parseUrl(MANIFEST_URL), Manifest.class);
                globalManifest.appVersion = SharedConstrains.APP_VERSION;
                setManifest(globalManifest);
            }
        } catch (Exception e) {
            exception = e;
        }
        updateForGlobalInterface.run(exception, this);

        save();
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
    private String parseUrl(String inputUrl) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(inputUrl);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        bufferedReader.close();
        return result.toString();
    }
}
