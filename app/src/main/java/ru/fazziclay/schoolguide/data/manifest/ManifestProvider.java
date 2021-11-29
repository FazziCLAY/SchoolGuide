package ru.fazziclay.schoolguide.data.manifest;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.data.schedule.Schedule;
import ru.fazziclay.schoolguide.util.FileUtil;
import ru.fazziclay.schoolguide.data.BaseData;
import ru.fazziclay.schoolguide.data.BaseProvider;

public class ManifestProvider extends BaseProvider {
    private static final String MANIFEST_LOCAL_FILE = "manifest.json";
    private static final String MANIFEST_URL = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/main/manifest/manifest.json";
    private static final String MANIFEST_KEY_URL = "https://raw.githubusercontent.com/FazziCLAY/SchoolGuide/main/manifest/manifest.json.key";

    public static final int CURRENT_FORMAT_VERSION = 4;

    public ManifestProvider(Context context) {
        filePath = context.getExternalCacheDir().getAbsolutePath().concat("/").concat(MANIFEST_LOCAL_FILE);
        data = load();

        new Thread(() -> updateForGlobal((exception, manifestProvider) -> {})).start();
    }

    @Override
    public BaseData load() {
        Gson gson = new Gson();
        return gson.fromJson(FileUtil.read(filePath, "{}"), Manifest.class);
    }

    public void updateForGlobal(UpdateForGlobalInterface updateForGlobalInterface) {
        new Thread(() -> {
            Gson gson = new Gson();
            Exception exception = null;

            try {
                int key = Integer.parseInt(parseUrl(MANIFEST_KEY_URL));

                if (getManifest().manifestKey != key) {
                    String parsed = parseUrl(MANIFEST_URL);
                    Manifest globalManifest = gson.fromJson(parsed, Manifest.class);
                    setManifest(globalManifest);
                    FileUtil.write(filePath, new JSONObject(parsed).toString(4));
                }
            } catch (Exception e) {
                exception = e;
            }
            updateForGlobalInterface.run(exception, ManifestProvider.this);
        }).start();
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
        AppVersion appVersion = SharedConstrains.APPLICATION_VERSION;

        if (latestVersion == null) {
            return VersionState.UNKNOWN;
        }

        if (appVersion.code == latestVersion.code) return VersionState.LATEST;
        if (appVersion.code < latestVersion.code) return VersionState.OUTDATED;
        return VersionState.UNKNOWN;
    }

    public boolean isTechnicalWorks() {
        return getManifest().isTechnicalWorks;
    }

    public Schedule getDeveloperSchedule() {
        return getManifest().developerSchedule;
    }

    public void setDeveloperSchedule(Schedule s) {
        getManifest().developerSchedule = s;
        save();
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
