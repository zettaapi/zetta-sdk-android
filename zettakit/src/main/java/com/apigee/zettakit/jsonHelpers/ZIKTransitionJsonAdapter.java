package com.apigee.zettakit.jsonHelpers;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKTransition;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ZIKTransitionJsonAdapter {
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
            type = "application/x-www-form-urlencoded";
        }
        List<Map> fields = transitionJson.fields;
        if( fields == null ) {
            fields = new ArrayList<>();
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
