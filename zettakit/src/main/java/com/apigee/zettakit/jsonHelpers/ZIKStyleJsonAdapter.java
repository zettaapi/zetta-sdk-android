package com.apigee.zettakit.jsonHelpers;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKStyle;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.HashMap;
import java.util.Map;

public final class ZIKStyleJsonAdapter {
    @FromJson
    public ZIKStyle styleFromJson(@NonNull final ZIKStyleJson styleJson) {
        Map properties = styleJson.properties;
        if( properties == null ) {
            properties = new HashMap();
        }
        Map actions = styleJson.actions;
        if( actions == null ) {
            actions = new HashMap();
        }
        return new ZIKStyle(properties,actions);
    }

    @ToJson
    public ZIKStyleJson styleToJson(@NonNull final ZIKStyle style) {
        ZIKStyleJson json = new ZIKStyleJson();
        json.properties = style.getProperties();
        json.actions = style.getActions();
        return json;
    }
}
