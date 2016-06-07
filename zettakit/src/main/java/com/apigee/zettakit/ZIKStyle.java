package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

public class ZIKStyle {
    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String FOREGROUND_COLOR = "foregroundColor";

    @NonNull private final Map<String,Object> properties;
    @NonNull private final Map<String,Object> actions;
    @Nullable private final ZIKStyleColor backgroundColor;
    @Nullable private final ZIKStyleColor foregroundColor;

    @NonNull public Map<String,Object> getProperties() { return this.properties; }
    @NonNull public Map<String,Object> getActions() { return this.actions; }
    @Nullable public ZIKStyleColor getBackgroundColor() { return this.backgroundColor; }
    @Nullable public ZIKStyleColor getForegroundColor() { return this.foregroundColor; }

    public ZIKStyle(@NonNull final Map<String,Object> properties, @NonNull final Map<String,Object> actions) {
        this.properties = properties;
        this.actions = actions;
        Object backgroundColorObject = this.properties.get(BACKGROUND_COLOR);
        if( backgroundColorObject != null && backgroundColorObject instanceof Map ) {
            this.backgroundColor = new ZIKStyleColor((Map)backgroundColorObject);
        } else {
            this.backgroundColor = null;
        }
        Object foregroundColorObject = this.properties.get(FOREGROUND_COLOR);
        if( foregroundColorObject != null && foregroundColorObject instanceof Map ) {
            this.foregroundColor = new ZIKStyleColor((Map)foregroundColorObject);
        } else {
            this.foregroundColor = null;
        }
    }
}
