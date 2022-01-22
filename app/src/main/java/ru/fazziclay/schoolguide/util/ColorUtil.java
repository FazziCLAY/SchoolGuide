package ru.fazziclay.schoolguide.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorUtil {
    public static String colorToHex(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return "#" + byteToHex(a) + byteToHex(r) + byteToHex(g) + byteToHex(b);
    }

    private static String byteToHex(int value) {
        String hex = "00".concat(Integer.toHexString(value));
        return hex.substring(hex.length()-2);
    }

    /**
     * &[] - system
     * &[0] - 0 - type
     * &[01] - 1 - value
     *
     * <code>"Hello &[-#ffffffff]&[=#66000000]"</code>
     * **/
    public static SpannableString colorize(String text, int defaultFgColor, int defaultBgColor, int defaultStyle) {
        if (text == null) return null;

        int currentForegroundSpan = defaultFgColor;
        int currentBackgroundSpan = defaultBgColor;
        int currentStyleSpan = defaultStyle;

        List<SpanText> spanTextList = new ArrayList<>();


        char[] chars = text.toCharArray();
        int oi = 0; // курсор по chars
        int ni = 0; // курсор по новой строке
        while (oi < chars.length) {
            Log.d("ColorUtil.colorize()", "startWhile oi=" + oi + "; ni=" + ni + "; cfg=" + currentForegroundSpan + "; cbg=" + currentBackgroundSpan + "; cs=" + currentStyleSpan);
            boolean appendOld = true;
            boolean appendNew = true;
            String toAppend = "";

            if (chars[oi] == '\\') {
                if (oi + 1 < chars.length && chars[oi + 1] == '$') {
                    toAppend = "$";
                    oi += 2;
                }
            } else if (chars[oi] == '$' && (oi - 1 < 0 || chars[oi] != '\\')) {
                if (oi + 1 < chars.length && chars[oi + 1] == '[') {
                    boolean closeSymbol = false;
                    int _i = oi + 2;
                    while (_i < chars.length) {
                        if (chars[_i] == ']') {
                            closeSymbol = true;
                            break;
                        }
                        _i++;
                    }
                    if (closeSymbol) {
                        if (oi+2 < chars.length) {
                            String[] systems = text.substring(oi+2, _i).split(";");
                            for (String system : systems) {
                                if (system.length() < 2) continue;
                                char systemType = system.charAt(0);
                                String systemValue = system.substring(1);
                                if (systemType == '-') {
                                    int color = Color.MAGENTA;
                                    try {
                                        color = Color.parseColor(systemValue);
                                    } catch (Exception ignored) {
                                        if (systemValue.equals("reset")) color = defaultFgColor;
                                    }
                                    currentForegroundSpan = color;

                                } else if (systemType == '=') {
                                    int color = Color.MAGENTA;
                                    try {
                                        color = Color.parseColor(systemValue);
                                    } catch (Exception ignored) {
                                        if (systemValue.equals("reset")) color = defaultBgColor;
                                    }
                                    currentBackgroundSpan = color;

                                } else if (systemType == '@') {
                                    int style = Typeface.NORMAL;
                                    switch (systemValue) {
                                        case "normal":
                                            style = Typeface.NORMAL;
                                            break;
                                        case "bolditalic":
                                        case "italicbold":
                                            style = Typeface.BOLD_ITALIC;
                                            break;
                                        case "bold":
                                            style = Typeface.BOLD;
                                            break;
                                        case "italic":
                                            style = Typeface.ITALIC;
                                            break;
                                    }
                                    if (systemValue.equals("reset")) style = defaultStyle;
                                    currentStyleSpan = style;
                                }
                            }
                        }
                        oi = _i;
                    }

                }

            } else {
                toAppend = String.valueOf(chars[oi]);
            }

            if (oi >= chars.length) continue;
            SpanText latestSpan = ListUtil.getLatestElement(spanTextList);
            if (spanTextList.size() > 0 && latestSpan != null & latestSpan.spanEquals(currentForegroundSpan, currentBackgroundSpan, currentStyleSpan)) {
                latestSpan.appendText(toAppend);
            } else {
                int latestStart = ni;
                if (latestSpan != null) {
                    latestStart = latestSpan.end;
                }
                SpanText n = new SpanText(toAppend, currentForegroundSpan, currentBackgroundSpan, currentStyleSpan, latestStart);
                spanTextList.add(n);
            }


            if (appendOld) oi++;
            if (appendNew) ni++;
        }

        StringBuilder fullText = new StringBuilder();
        for (SpanText spanText : spanTextList) {
            fullText.append(spanText.text);
        }

        SpannableString spannableText = new SpannableString(fullText.toString());
        Log.d("SpannableText", spannableText.toString());
        Log.d("SpanTextList", Arrays.toString(spanTextList.toArray()));
        int i = 0;
        while (i < spanTextList.size()) {
            SpanText spanText = spanTextList.get(i);
            int start = Math.min(spanText.start, spannableText.length());
            int end = Math.min(spanText.end, spannableText.length());

            spannableText.setSpan(new ForegroundColorSpan(spanText.fgColor), start, end, Spannable.SPAN_COMPOSING);
            spannableText.setSpan(new BackgroundColorSpan(spanText.bgColor), start, end, Spanned.SPAN_COMPOSING);
            spannableText.setSpan(new StyleSpan(spanText.style), start, end, Spanned.SPAN_COMPOSING);
            i++;
        }
        return spannableText;
    }

    public static class SpanText {
        public String text;
        public int fgColor;
        public int bgColor;
        public int style;

        public int start;
        public int end;

        public SpanText(String text, int fgColor, int bgColor, int style, int start) {
            this.text = text;
            this.fgColor = fgColor;
            this.bgColor = bgColor;
            this.style = style;
            this.start = start;
            this.end = start + text.length();
        }

        public void appendText(String s) {
            text = text + s;
            end = start + text.length();
        }

        public boolean spanEquals(int fgColor, int bgColor, int style) {
            return (this.fgColor == fgColor && this.bgColor == bgColor && this.style == style);
        }

        @NonNull
        @Override
        public String toString() {
            return "SpanText{" +
                    "text='" + text + '\'' +
                    ", fgColor=" + fgColor +
                    ", bgColor=" + bgColor +
                    ", style=" + style +
                    ", start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
}
