package ru.fazziclay.schoolguide.data.jsonparser;

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
    @SerializedName("8")
    String _8;
    @SerializedName("9")
    String _9;
    @SerializedName("10")
    String _10;

    public JsonDefaultStartTime(String _1,
                                String _2,
                                String _3,
                                String _4,
                                String _5,
                                String _6,
                                String _7,
                                String _8,
                                String _9,
                                String _10) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
        this._5 = _5;
        this._6 = _6;
        this._7 = _7;
        this._8 = _8;
        this._9 = _9;
        this._10 = _10;
    }

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
        if (i == 8) return _8;
        if (i == 9) return _9;
        if (i == 10) return _10;
        return null;
    }
}
