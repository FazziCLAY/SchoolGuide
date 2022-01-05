package ru.fazziclay.schoolguide.app;

import java.io.File;

import ru.fazziclay.schoolguide.util.data.BaseData;

public class Settings extends BaseData {
    public static final String FILE = "schoolguide.settings.json";

    public static Settings load(File file) {
        return (Settings) load(file, Settings.class);
    }

    public boolean developerFeatures = false;

    @Override
    public void reset() {
        developerFeatures = false;
    }
}
