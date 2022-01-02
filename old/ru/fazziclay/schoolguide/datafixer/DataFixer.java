package ru.fazziclay.schoolguide.datafixer;

import android.content.Context;

import java.io.File;

import ru.fazziclay.schoolguide.CrashReport;
import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.datafixer.schema.Schema;
import ru.fazziclay.schoolguide.datafixer.schema.SchemaV33OLD;
import ru.fazziclay.schoolguide.util.FileUtil;

public class DataFixer {
    private static final Schema[] SCHEMAS = {
        new SchemaV33OLD()
    };

    private static final String VERSION_EMPTY = "";
    private static final int VERSION_UNKNOWN = -1;
    private static final int MAX_REPEATS = 1000;

    Context context;
    int version;

    String PATH_VERSION;

    public DataFixer(Context context) {
        this.context = context;
        this.PATH_VERSION = context.getExternalFilesDir(null).getAbsolutePath() + "/" + "version";
    }

    public void tryFix() {
        version = detectGenericVersion();

        int i = 0;
        while (i < MAX_REPEATS) {
            boolean none = true;
            for (Schema schema : SCHEMAS) {
                if (schema.isCompatibly(version)) {
                    try {
                        version = schema.run();
                    } catch (Exception exception) {
                        try {
                            new CrashReport(context, exception);
                        } catch (Exception ignored) {}
                    }
                    none = false;
                }
            }

            if (none || version >= SharedConstrains.APPLICATION_VERSION_CODE) break;
            i++;
        }

        FileUtil.write(PATH_VERSION, String.valueOf(SharedConstrains.APPLICATION_VERSION_CODE));
    }

    public int detectGenericVersion() {
        String PATH_SCHEDULE = context.getExternalFilesDir(null).getAbsolutePath() + "/" + "schedule.json";
        String v = FileUtil.read(PATH_VERSION, VERSION_EMPTY);
        boolean isScheduleExist = new File(PATH_SCHEDULE).exists();
        if (VERSION_EMPTY.equals(v)) {
            if (isScheduleExist) {
                return 33;
            } else {
                return SharedConstrains.APPLICATION_VERSION_CODE;
            }
        } else {
            try {
                return Integer.parseInt(v);
            } catch (Exception ignored) {
                if (isScheduleExist) {
                    return 33;
                }
            }
        }
        return VERSION_UNKNOWN;
    }
}
