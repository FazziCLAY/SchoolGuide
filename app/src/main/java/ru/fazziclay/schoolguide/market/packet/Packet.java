package ru.fazziclay.schoolguide.market.packet;

import com.google.gson.Gson;

public class Packet {
    public byte[] write() {
        Gson gson = new Gson();
        String a = gson.toJson(this, this.getClass());
        return a.getBytes();
    }

    public static Packet parse(byte[] bytes, Class<? extends Packet> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(new String(bytes), clazz);
    }
}
