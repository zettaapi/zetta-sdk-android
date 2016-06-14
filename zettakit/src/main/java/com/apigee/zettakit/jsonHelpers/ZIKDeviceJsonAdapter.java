package com.apigee.zettakit.jsonHelpers;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKDevice;
import com.apigee.zettakit.ZIKException;
import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKStyle;
import com.apigee.zettakit.ZIKTransition;
import com.apigee.zettakit.utils.ZIKJsonUtils;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ZIKDeviceJsonAdapter {
    private static final String STYLE = "style";

    @FromJson
    public ZIKDevice deviceFromJson(@NonNull final ZIKDeviceJson deviceJson) {
        Map<String,Object> propertiesMap = deviceJson.properties;
        if( propertiesMap == null ) {
            propertiesMap = Collections.emptyMap();
        }
        List<ZIKLink> links = deviceJson.links;
        if( links == null ) {
            links = Collections.emptyList();
        }
        List<ZIKTransition> actions = deviceJson.actions;
        if( actions == null ) {
            actions = Collections.emptyList();
        }
        ZIKStyle style = null;
        Object styleObject = propertiesMap.get(STYLE);
        if( styleObject != null && styleObject instanceof Map ) {
            try {
                style = ZIKJsonUtils.convertJsonMapToObject(ZIKStyle.class,(Map)styleObject);
            } catch( ZIKException ignored ) {}
        }
        return new ZIKDevice(propertiesMap,links,actions,style);
    }

    @ToJson
    public ZIKDeviceJson deviceToJson(@NonNull final ZIKDevice device) {
        ZIKDeviceJson json = new ZIKDeviceJson();
        json.properties = device.getProperties();
        json.links = device.getLinks();
        json.actions = device.getTransitions();
        return json;
    }
}
