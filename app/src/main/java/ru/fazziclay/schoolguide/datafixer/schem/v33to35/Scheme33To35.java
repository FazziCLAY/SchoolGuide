package ru.fazziclay.schoolguide.datafixer.schem.v33to35;

import ru.fazziclay.schoolguide.datafixer.schem.AbstractScheme;

public class Scheme33To35 extends AbstractScheme {
    @Override
    public boolean isCompatible(int version) {
        return version <= 33;
    }

    @Override
    public int run(int version) {

        return 35;
    }
}
