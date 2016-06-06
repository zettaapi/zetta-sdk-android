package com.apigee.zettakit;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ZIKStyleColor {
    @NonNull private final String hex;
    @NonNull private final Map<String,Integer> decimal;
    private final int color;

    public ZIKStyleColor(@JsonProperty("hex") @NonNull final String hex, @JsonProperty("decimal") @NonNull final Map<String,Integer> decimal) {
        this.hex = hex;
        this.color = Color.parseColor(this.hex);
        this.decimal = decimal;
    }

    @NonNull
    public String getHex() {
        return this.hex;
    }

    @NonNull
    public Map<String,Integer> getDecimal() {
        return this.decimal;
    }

    public int color() {
        return this.color;
    }

    public int red() {
        return this.decimal.get("red");
    }

    public int green() {
        return this.decimal.get("green");
    }

    public int blue() {
        return this.decimal.get("blue");
    }
}
