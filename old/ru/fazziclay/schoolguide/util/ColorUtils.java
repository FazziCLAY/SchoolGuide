package ru.fazziclay.schoolguide.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.List;

public class ColorUtils {
    public static final String STRING_CYAN = ColorUtils.colorToHex(Color.CYAN);

    public static String colorToHex(int color) {
        int alpha = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return "#" + byteToHex(alpha) + byteToHex(r) + byteToHex(g) + byteToHex(b);
    }

    public static int parseColor(String color, String defaultColor) {
        try {
            return Color.parseColor(color);
        } catch (Exception ignored) {
            return Color.parseColor(defaultColor);
        }
    }

    static String byteToHex(int value) {
        String hex = "00".concat(Integer.toHexString(value));
        return hex.substring(hex.length()-2);
    }

    public static Spannable colorizeText(String source, int defaultColor) {
        return colorizeText(source, defaultColor, true);
    }

    public static Spannable colorizeText(String source, int defaultColor, boolean remove) {
        if (source == null) return null;
        try {
            return colorizeTextW(source, defaultColor, remove);
        } catch (Exception ignored) {}
        return new SpannableString(source);
    }

    public static Spannable colorizeTextW(String source, int defaultColor, boolean remove) {
        if (source == null) return null;
        StringBuilder newString = new StringBuilder();
        List<SpannableStyle> span = new ArrayList<>();

        int i = 0;
        int u = 0;
        int y = 0;
        int color = defaultColor;
        int style = Typeface.NORMAL;

        while (i < source.length()) {
            if (source.charAt(i) == '&') {
                if (i+1 == (source.length())) break;
                int[] span1 = spanFromCharCode(source.charAt(i+1), defaultColor);
                if (span1[0] == 1) {
                    if ((style == Typeface.BOLD || span1[1] == Typeface.ITALIC) && (span1[1] == Typeface.BOLD || span1[1] == Typeface.ITALIC)) {
                        style = Typeface.BOLD_ITALIC;
                    } else {
                        if (span1[1] == 1000) {
                            style = Typeface.NORMAL;
                            color = defaultColor;
                        } else {
                            style = span1[1];
                        }
                    }

                } else if (span1[0] == 2) {
                    color = span1[1];
                }
                u+=2;
            }

            if (u == 0 || !remove) {
                newString.append(source.charAt(i));
                span.add(new SpannableStyle(new ForegroundColorSpan(color), y, y + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE));
                span.add(new SpannableStyle(new StyleSpan(style), y, y + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE));

                y++;
            }
            if (u > 0) u--;
            i++;
        }

        Spannable spannableString = new SpannableString(newString);
        for (SpannableStyle spannableStyle : span) {
            spannableString.setSpan(spannableStyle.what, spannableStyle.start, spannableStyle.end, spannableStyle.flags);
        }

        return spannableString;
    }

    public static int[] spanFromCharCode(char code, int defaultCode) {
        int type = 1;
        int value = -999;

        switch (code) {
            case 'l':
                value = Typeface.BOLD;
                break;

            case 'o':
                value = Typeface.ITALIC;
                break;

            case 'r':
                value = 1000;
                break;
        }

        if (value == -999) {
            type = 2;
            value = colorFromCharCode(code, defaultCode);
        }

        return new int[]{type, value};
    }

    public static int colorFromCharCode(char code, int defaultColor) {
        switch (code) {
            case '0':
                return Color.rgb(0,0,0);

            case '1':
                return Color.rgb(0,0,170);

            case '2':
                return Color.rgb(0,170,0);

            case '3':
                return Color.rgb(0,170,170);

            case '4':
                return Color.rgb(170,0,0);

            case '5':
                return Color.rgb(170,0,170);

            case '6':
                return Color.rgb(255, 170, 0);

            case '7':
                return Color.rgb(170, 170, 170);

            case '8':
                return Color.rgb(85, 85, 85);

            case '9':
                return Color.rgb(85, 85, 255);

            case 'a':
                return Color.rgb(85, 255, 85);

            case 'b':
                return Color.rgb(85, 255, 255);

            case 'c':
                return Color.rgb(255, 85, 85);

            case 'd':
                return Color.rgb(255, 85, 255);

            case 'e':
                return Color.rgb(255, 255, 85);

            case 'f':
                return Color.rgb(255, 255, 255);


            default:
                return defaultColor;
        }
    }

    public static class SpannableStyle {
        public Object what;
        public int start;
        public int end;
        public int flags;

        public SpannableStyle(Object what, int start, int end, int flags) {
            this.what = what;
            this.start = start;
            this.end = end;
            this.flags = flags;
        }
    }
}
