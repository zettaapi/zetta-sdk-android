package com.apigee.zettakit;

import android.graphics.Color;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ZIKStyleColor {
    private static final String HEX = "hex";
    private static final String DECIMAL = "decimal";
    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";

    private static int DEFAULT_COLOR = Color.BLACK;
    public static void setDefaultColor(final int color) { ZIKStyleColor.DEFAULT_COLOR = color; }

    @NonNull private final String hex;
    @NonNull private final Map<String,Integer> decimal;
    private final int color;

    @NonNull public String getHex() { return this.hex; }
    @NonNull public Map<String,Integer> getDecimal() { return this.decimal; }

    public int color() { return this.color; }
    public int red() { return this.decimal.get(RED); }
    public int green() {
        return this.decimal.get(GREEN);
    }
    public int blue() {
        return this.decimal.get(BLUE);
    }

    @SuppressWarnings("unchecked")
    public ZIKStyleColor(@NonNull final Map styleColorMap) {
        Object hexObject = styleColorMap.get(HEX);
        if( hexObject != null && hexObject instanceof String ) {
            this.hex = (String)hexObject;
            this.color = Color.parseColor(this.hex);
        } else {
            this.hex = Integer.toHexString(DEFAULT_COLOR);
            this.color = DEFAULT_COLOR;
        }
        Object decimalObject = styleColorMap.get(DECIMAL);
        if( decimalObject != null && decimalObject instanceof Map ) {
            this.decimal = (Map)decimalObject;
        } else {
            HashMap<String,Integer> decimalMap = new HashMap<String,Integer>();
            decimalMap.put(RED,Color.red(this.color));
            decimalMap.put(GREEN,Color.green(this.color));
            decimalMap.put(BLUE,Color.blue(this.color));
            this.decimal = decimalMap;
        }
    }
}
