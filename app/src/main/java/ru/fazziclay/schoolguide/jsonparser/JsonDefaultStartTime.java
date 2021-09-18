package ru.fazziclay.schoolguide.jsonparser;

import com.google.gson.annotations.SerializedName;

public class JsonDefaultStartTime {
    @SerializedName("1")
    String _1;
    @SerializedName("2")
    String _2;
    @SerializedName("3")
    String _3;
    @SerializedName("4")
    String _4;
    @SerializedName("5")
    String _5;
    @SerializedName("6")
    String _6;
    @SerializedName("7")
    String _7;

    public int toMillis(String v) {
        String[] a = v.split(":");
        int hour = Integer.parseInt(a[0]);
        int minute = Integer.parseInt(a[1]);

        return (((hour * 60) + minute)*60*1000);
    }

    public String get(int i) {
        if (i == 1) return _1;
        if (i == 2) return _2;
        if (i == 3) return _3;
        if (i == 4) return _4;
        if (i == 5) return _5;
        if (i == 6) return _6;
        if (i == 7) return _7;
        return null;
    }
}
