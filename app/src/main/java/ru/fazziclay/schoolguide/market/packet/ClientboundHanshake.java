package ru.fazziclay.schoolguide.market.packet;

import java.util.UUID;

public class ClientboundHanshake extends Packet {
    public static final short ID = 1;

    UUID instanceUUID;

    public ClientboundHanshake(UUID instanceUUID) {
        this.instanceUUID = instanceUUID;
    }

    public UUID getInstanceUUID() {
        return instanceUUID;
    }
}
