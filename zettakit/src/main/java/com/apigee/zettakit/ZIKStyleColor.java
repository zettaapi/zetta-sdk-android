package com.apigee.zettakit;

import android.graphics.Color;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ZIKStyleColor {
    private static int DEFAULT_COLOR = Color.BLACK;
    public static void setDefaultColor(final int color) { ZIKStyleColor.DEFAULT_COLOR = color; }

    @NonNull private final String hex;
    @NonNull private final Map<String,Integer> decimal;
    private final int color;

    @NonNull public String getHex() { return this.hex; }
    @NonNull public Map<String,Integer> getDecimal() { return this.decimal; }

    public int color() { return this.color; }
    public int red() {
        return this.decimal.get("red");
    }
    public int green() {
        return this.decimal.get("green");
    }
    public int blue() {
        return this.decimal.get("blue");
    }

    @SuppressWarnings("unchecked")
    public ZIKStyleColor(@NonNull final Map styleColorMap) {
        Object hexObject = styleColorMap.get("hex");
        if( hexObject != null && hexObject instanceof String ) {
            this.hex = (String)hexObject;
            this.color = Color.parseColor(this.hex);
        } else {
            this.hex = Integer.toHexString(DEFAULT_COLOR);
            this.color = DEFAULT_COLOR;
        }
        Object decimalObject = styleColorMap.get("decimal");
        if( decimalObject != null && decimalObject instanceof Map ) {
            this.decimal = (Map)decimalObject;
        } else {
            HashMap<String,Integer> decimalMap = new HashMap<>();
            decimalMap.put("red",Color.red(this.color));
            decimalMap.put("green",Color.green(this.color));
            decimalMap.put("blue",Color.blue(this.color));
            this.decimal = decimalMap;
        }
    }
}
