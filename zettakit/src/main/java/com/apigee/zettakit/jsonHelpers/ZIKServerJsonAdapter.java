package com.apigee.zettakit.jsonHelpers;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKDevice;
import com.apigee.zettakit.ZIKException;
import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKServer;
import com.apigee.zettakit.ZIKStyle;
import com.apigee.zettakit.ZIKTransition;
import com.apigee.zettakit.utils.ZIKJsonUtils;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ZIKServerJsonAdapter {
    private static final String STYLE = "style";

    @FromJson
    public ZIKServer severFromJson(@NonNull final ZIKServerJson serverJson) {
        Map<String,Object> propertiesMap = serverJson.properties;
        if( propertiesMap == null ) {
            propertiesMap = Collections.emptyMap();
        }
        List<ZIKDevice> devices = serverJson.entities;
        if( devices == null ) {
            devices = Collections.emptyList();
        }
        List<ZIKLink> links = serverJson.links;
        if( links == null ) {
            links = Collections.emptyList();
        }
        List<ZIKTransition> actions = serverJson.actions;
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
        return new ZIKServer(propertiesMap,devices,links,actions,style);
    }

    @ToJson
    public ZIKServerJson serverToJson(@NonNull final ZIKServer server) {
        ZIKServerJson json = new ZIKServerJson();
        json.properties = server.getProperties();
        json.entities = server.getDevices();
        json.links = server.getLinks();
        json.actions = server.getTransitions();
        return json;
    }
}
