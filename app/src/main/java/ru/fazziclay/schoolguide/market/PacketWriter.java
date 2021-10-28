package ru.fazziclay.schoolguide.market;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class PacketWriter {
    OutputStream outputStream;

    public PacketWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(short packetId, byte[] data) throws IOException {
        int length = data.length;
        outputStream.write(getBytes(length));
        outputStream.write(getBytes(packetId));
        outputStream.write(data);
        outputStream.flush();
    }

    public static byte[] getBytes(short value) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort(value);
        return bb.array();
    }

    public static byte[] getBytes(int value) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(value);
        return bb.array();
    }
}
