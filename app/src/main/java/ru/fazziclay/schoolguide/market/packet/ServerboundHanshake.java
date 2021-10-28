package ru.fazziclay.schoolguide.market.packet;

import java.util.UUID;

public class ServerboundHanshake extends Packet {
    public static final short ID = 2;

    int versionCode;
    UUID instanceUUID;

    public ServerboundHanshake(int versionCode, UUID instanceUUID) {
        this.versionCode = versionCode;
        this.instanceUUID = instanceUUID;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public UUID getInstanceUUID() {
        return instanceUUID;
    }
}
