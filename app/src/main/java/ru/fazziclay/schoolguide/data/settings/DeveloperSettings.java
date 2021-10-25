package ru.fazziclay.schoolguide.data.settings;

import java.util.ArrayList;
import java.util.List;

public class DeveloperSettings {
    public boolean isEnable = false;
    public boolean startForegroundService = true;
    public boolean startHomeActivity = true;
    public boolean externalLoading = false;
    public List<ExternalLoading> externalLoadings = new ArrayList<>();
}
