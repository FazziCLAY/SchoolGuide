package ru.fazziclay.schoolguide.datafixer.schem;

import android.content.Context;

public abstract class AbstractScheme {
    public abstract boolean isCompatible(int version);

    public abstract int run(Context context, int version);
}
