package ru.fazziclay.schoolguide.app.manifest;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.util.DataUtil;
import ru.fazziclay.schoolguide.util.FileUtil;
import ru.fazziclay.schoolguide.util.NetworkUtil;

public class GlobalManager {
    public static void get(Context context, GlobalManagerInterface globalManagerInterface) {
        Thread thread = new Thread(() -> {
            Gson gson = new Gson();
            File cacheDir = context.getExternalCacheDir();
            File localVersionManifestFile = new File(cacheDir, "local.versionManifest.json");
            File localBuiltinScheduleFile = new File(cacheDir, "local.builtinSchedule.json");

            try {
                String keysStr = NetworkUtil.parseTextPage(SharedConstrains.KEYS_V2);
                GlobalKeys globalKeys = gson.fromJson(keysStr, GlobalKeys.class);

                GlobalVersionManifest versionManifest = DataUtil.load(localVersionManifestFile, GlobalVersionManifest.class);
                GlobalBuiltinSchedule builtinSchedule = DataUtil.load(localBuiltinScheduleFile, GlobalBuiltinSchedule.class);

                if (globalKeys.versionManifest != versionManifest.key) {
                    String versionManifestStr = NetworkUtil.parseTextPage(SharedConstrains.VERSION_MANIFEST_V2);
                    versionManifest = gson.fromJson(versionManifestStr, GlobalVersionManifest.class);
                    FileUtil.write(localVersionManifestFile, versionManifestStr);
                }

                if (globalKeys.builtinSchedule != builtinSchedule.key) {
                    String builtinScheduleStr = NetworkUtil.parseTextPage(SharedConstrains.BUILTIN_SCHEDULE_V2);
                    builtinSchedule = gson.fromJson(builtinScheduleStr, GlobalBuiltinSchedule.class);
                    FileUtil.write(localBuiltinScheduleFile, builtinScheduleStr);
                }

                globalManagerInterface.success(globalKeys, versionManifest, builtinSchedule);
            } catch (Exception e) {
                e.printStackTrace();
                globalManagerInterface.failed(e);
            }
        });
        thread.setName("GlobalManager-get");
        thread.start();
    }

    public interface GlobalManagerInterface {
        void failed(Exception exception);
        void success(GlobalKeys keys, GlobalVersionManifest versionManifest, GlobalBuiltinSchedule builtinSchedule);
    }
}
