package ru.fazziclay.schoolguide.datafixer.schem;

import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.datafixer.Version;

/**
 * Схема имеет 2 метода, это подходит ли эта схема для определённой версии и собственно метод запуска
 * @see DataFixer
 * **/
public abstract class AbstractScheme {
    public abstract boolean isCompatible(Version version);

    public abstract Version run(DataFixer dataFixer, Version version);
}
