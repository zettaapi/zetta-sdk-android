package com.apigee.zettakit.jsonHelpers;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKRoot;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ZIKRootJsonAdapter {
    @FromJson
    public ZIKRoot rootFromJson(@NonNull final ZIKRootJson rootJson) {
        List<ZIKLink> links = rootJson.links;
        if( links == null ) {
            links = new ArrayList<>();
        }
        List<Map> actions = rootJson.actions;
        if( actions == null ) {
            actions = new ArrayList<>();
        }
        return new ZIKRoot(links,actions);
    }

    @ToJson
    public ZIKRootJson rootToJson(@NonNull final ZIKRoot root) {
        ZIKRootJson json = new ZIKRootJson();
        json.links = root.getLinks();
        json.actions = root.getActions();
        return json;
    }
}
