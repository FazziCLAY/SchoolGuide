package ru.fazziclay.schoolguide.datafixer.schem;

import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.datafixer.Version;

public abstract class AbstractScheme {
    public abstract boolean isCompatible(Version version);

    public abstract Version run(DataFixer dataFixer, Version version);
}
