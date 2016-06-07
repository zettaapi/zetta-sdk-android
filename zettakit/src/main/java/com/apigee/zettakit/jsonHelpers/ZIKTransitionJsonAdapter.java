package com.apigee.zettakit.jsonHelpers;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKTransition;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ZIKTransitionJsonAdapter {
    private static final String DEFAULT_TYPE = "application/x-www-form-urlencoded";

    @FromJson
    public ZIKTransition transitionFromJson(@NonNull final ZIKTransitionJson transitionJson) {
        String href = transitionJson.href;
        if( href == null ) {
            href = "";
        }
        String name = transitionJson.name;
        if( name == null ) {
            name = "";
        }
        String method = transitionJson.method;
        if( method == null ) {
            method = "";
        }
        String type = transitionJson.type;
        if( type == null ) {
            type = DEFAULT_TYPE;
        }
        List<Map<String,Object>> fields = transitionJson.fields;
        if( fields == null ) {
            fields = Collections.emptyList();
        }
        return new ZIKTransition(href,name,method,type,fields);
    }

    @ToJson
    public ZIKTransitionJson transitionToJson(@NonNull final ZIKTransition transition) {
        ZIKTransitionJson json = new ZIKTransitionJson();
        json.href = transition.getHref();
        json.name = transition.getName();
        json.method = transition.getMethod();
        json.type = transition.getType();
        json.fields = transition.getFields();
        return json;
    }
}
