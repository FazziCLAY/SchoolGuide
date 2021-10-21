package ru.fazziclay.schoolguide.data;

public abstract class BaseProvider {
    protected BaseData data;   // Data object
    protected String filePath; // Path to file

    protected BaseProvider() {}

    public void save() {
        data.save(filePath);
    }

    public BaseData load() {
        throw new Error("Called not-override method!");
    }
}
