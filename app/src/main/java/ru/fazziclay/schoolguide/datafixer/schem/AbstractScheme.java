package ru.fazziclay.schoolguide.datafixer.schem;

public abstract class AbstractScheme {
    public abstract boolean isCompatible(int version);

    public abstract int run(int version);
}
