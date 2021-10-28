package ru.fazziclay.schoolguide.market;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketParser {
    InputStream inputStream;
    PacketParserInterface packetParserInterface;

    State state = State.WAIT_LENGTH;
    int length = 0;
    short packetId = 0;

    public PacketParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setPacketParserInterface(PacketParserInterface packetParserInterface) {
        this.packetParserInterface = packetParserInterface;
    }

    public void parse() throws IOException {
        if (state == State.WAIT_LENGTH && inputStream.available() >= 4) {
            byte[] lengthBuffer = new byte[4];
            inputStream.read(lengthBuffer, 0, 4);
            length = PacketParser.getInt(lengthBuffer);
            state = State.WAIT_ID;
        }

        if (state == State.WAIT_ID && inputStream.available() >= 2) {
            byte[] idBuffer = new byte[2];
            inputStream.read(idBuffer, 0, 2);
            packetId = getShort(idBuffer);
            state = State.WAIT_DATA;
        }

        if (state == State.WAIT_DATA && inputStream.available() >= length) {
            byte[] dataBuffer = new byte[length];
            inputStream.read(dataBuffer, 0, length);
            state = State.WAIT_LENGTH;
            packetParserInterface.onPacket(packetId, dataBuffer);
        }
    }

    public static short getShort(byte byte1, byte byte2) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(byte1);
        bb.put(byte2);
        return bb.getShort(0);
    }

    public static short getShort(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (byte b : bytes) {
            bb.put(b);
        }
        return bb.getShort(0);
    }

    public static int getInt(byte byte1, byte byte2, byte byte3, byte byte4) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(byte1);
        bb.put(byte2);
        bb.put(byte3);
        bb.put(byte4);
        return bb.getInt(0);
    }

    public static int getInt(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (byte b : bytes) {
            bb.put(b);
        }
        return bb.getInt(0);
    }

    public interface PacketParserInterface {
        void onPacket(short packetId, byte[] packetData);
    }

    public enum State {
        WAIT_LENGTH,
        WAIT_ID,
        WAIT_DATA
    }
}
