package ru.fazziclay.schoolguide.app.global;

import ru.fazziclay.schoolguide.app.scheduleinformator.appschedule.PresetList;

public class GlobalBuiltinPresetList extends PresetList implements GlobalData {
    public int key = 0;

    @Override
    public int getGlobalKey() {
        return key;
    }
}
