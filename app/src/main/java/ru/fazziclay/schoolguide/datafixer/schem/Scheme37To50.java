package ru.fazziclay.schoolguide.datafixer.schem;

import java.io.File;

import ru.fazziclay.schoolguide.app.MilkLog;
import ru.fazziclay.schoolguide.datafixer.DataFixer;
import ru.fazziclay.schoolguide.datafixer.Version;
import ru.fazziclay.schoolguide.datafixer.old.v37.V37MathTreningGameData;
import ru.fazziclay.schoolguide.datafixer.old.v50.V50MathTreningGameData;
import ru.fazziclay.schoolguide.util.FileUtil;

public class Scheme37To50 extends AbstractScheme {
    private static final String MATH_TRENING_FILE = "math_trening_game.json";

    @Override
    public boolean isCompatible(Version version) {
        int x = version.getLatestVersion();
        return x >= 36 && x < 50;
    }

    @Override
    public Version run(DataFixer dataFixer, Version version) {
        try {
            File mathTreningFile = new File(dataFixer.getAndroidContext().getExternalFilesDir(null), MATH_TRENING_FILE);
            if (mathTreningFile.exists()) {
                V37MathTreningGameData outdated = dataFixer.getGson().fromJson(FileUtil.read(mathTreningFile), V37MathTreningGameData.class);
                V50MathTreningGameData updated = new V50MathTreningGameData();
                updated.action = outdated.action == null ? "*" : outdated.action;
                updated.score = outdated.score;
                updated.firstNumberGenerator = new V50MathTreningGameData.GenRange();
                updated.firstNumberGenerator.minimum = outdated.n1RangeMin;
                updated.firstNumberGenerator.maximum = outdated.n1RangeMax;
                updated.latestNumberGenerator = new V50MathTreningGameData.GenRange();
                updated.latestNumberGenerator.minimum = outdated.n2RangeMin;
                updated.latestNumberGenerator.maximum = outdated.n2RangeMax;
                FileUtil.write(mathTreningFile, dataFixer.getGson().toJson(updated, V50MathTreningGameData.class));
                MilkLog.g("Math trening gameData converted!");
            }
        } catch (Exception e) {
            MilkLog.g("Exception while converting v37 mathTrening to v50 mathTrening!", e);
        }

        version.setLatestVersion(50);
        return version;
    }
}
