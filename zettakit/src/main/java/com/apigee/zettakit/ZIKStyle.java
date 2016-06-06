package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class ZIKStyle {
    @NonNull private final HashMap<String,JsonNode> properties;
    @Nullable private final HashMap<String,JsonNode> actions;
    @Nullable private final ZIKStyleColor backgroundColor;
    @Nullable private final ZIKStyleColor foregroundColor;

    @NonNull
    public Map<String,JsonNode> getProperties() { return this.properties; }

    @Nullable
    public Map<String,JsonNode> getActions() { return this.actions; }

    @Nullable @JsonIgnore
    public ZIKStyleColor getBackgroundColor() { return this.backgroundColor; }

    @Nullable @JsonIgnore
    public ZIKStyleColor getForegroundColor() { return this.foregroundColor; }

    public ZIKStyle(@JsonProperty("properties") @NonNull final HashMap<String,JsonNode> properties, @JsonProperty("actions") @Nullable final HashMap<String,JsonNode> actions) {
        this.properties = properties;
        this.actions = actions;
        JsonNode backgroundColor = this.properties.get("backgroundColor");
        if( backgroundColor != null ) {
            this.backgroundColor = ZIKSession.jsonMapper.convertValue(backgroundColor,ZIKStyleColor.class);
        } else {
            this.backgroundColor = null;
        }
        JsonNode foregroundColor = this.properties.get("foregroundColor");
        if( foregroundColor != null ) {
            this.foregroundColor = ZIKSession.jsonMapper.convertValue(foregroundColor,ZIKStyleColor.class);
        } else {
            this.foregroundColor = null;
        }
    }
}
