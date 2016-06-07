package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

public class ZIKStyle {
    @NonNull private final Map properties;
    @NonNull private final Map actions;
    @Nullable private final ZIKStyleColor backgroundColor;
    @Nullable private final ZIKStyleColor foregroundColor;

    @NonNull public Map getProperties() { return this.properties; }
    @NonNull public Map getActions() { return this.actions; }
    @Nullable public ZIKStyleColor getBackgroundColor() { return this.backgroundColor; }
    @Nullable public ZIKStyleColor getForegroundColor() { return this.foregroundColor; }

    public ZIKStyle(@NonNull final Map properties, @NonNull final Map actions) {
        this.properties = properties;
        this.actions = actions;
        Object backgroundColorObject = this.properties.get("backgroundColor");
        if( backgroundColorObject != null && backgroundColorObject instanceof Map ) {
            this.backgroundColor = new ZIKStyleColor((Map)backgroundColorObject);
        } else {
            this.backgroundColor = null;
        }
        Object foregroundColorObject = this.properties.get("foregroundColor");
        if( foregroundColorObject != null && foregroundColorObject instanceof Map ) {
            this.foregroundColor = new ZIKStyleColor((Map)foregroundColorObject);
        } else {
            this.foregroundColor = null;
        }
    }
}
