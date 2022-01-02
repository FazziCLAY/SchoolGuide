package ru.fazziclay.schoolguide.datafixer.schema;

public abstract class Schema {
    public abstract boolean isCompatibly(int version);
    public abstract int run();
}
