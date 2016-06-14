package com.apigee.zettakit.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.apigee.zettakit.ZIKException;
import com.apigee.zettakit.jsonHelpers.ZIKDeviceJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKLinkJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKLogStreamEntryJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKRootJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKServerJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKStreamEntryJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKStyleJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKTransitionJsonAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ZIKJsonUtils {
    private static final Moshi moshi;
    static {
        Moshi.Builder moshiBuilder = new Moshi.Builder();
        moshiBuilder.add(new ZIKDeviceJsonAdapter());
        moshiBuilder.add(new ZIKLinkJsonAdapter());
        moshiBuilder.add(new ZIKLogStreamEntryJsonAdapter());
        moshiBuilder.add(new ZIKRootJsonAdapter());
        moshiBuilder.add(new ZIKServerJsonAdapter());
        moshiBuilder.add(new ZIKStreamEntryJsonAdapter());
        moshiBuilder.add(new ZIKStyleJsonAdapter());
        moshiBuilder.add(new ZIKTransitionJsonAdapter());
        moshi = moshiBuilder.build();
    }

    private static <T> JsonAdapter<T> jsonAdapter(@NonNull final Class<T> objectClass) {
        return moshi.adapter(objectClass);
    }

    @NonNull
    public static <T> T createObjectFromJson(@NonNull final Class<T> objectClass, @NonNull final String jsonString) throws ZIKException {
        try {
            return ZIKJsonUtils.jsonAdapter(objectClass).fromJson(jsonString);
        } catch( IOException e ) {
            throw new ZIKException(e);
        }
    }

    @NonNull
    public static <T> T convertJsonMapToObject(@NonNull final Class<T> objectClass, @NonNull final Map jsonMap) throws ZIKException {
        return ZIKJsonUtils.createObjectFromJson(objectClass,ZIKJsonUtils.mapToJsonString(jsonMap));
    }

    @NonNull
    public static <T> String objectToJsonString(@NonNull final Class<T> objectClass, @NonNull final T object) {
        return ZIKJsonUtils.jsonAdapter(objectClass).toJson(object);
    }

    @NonNull
    public static String mapToJsonString(@NonNull final Map jsonMap) {
        return ZIKJsonUtils.objectToJsonString(Map.class,jsonMap);
    }

    @NonNull
    public static Map jsonStringToMap(@NonNull final String string) {
        Map map;
        try {
            map = ZIKJsonUtils.jsonAdapter(Map.class).fromJson(string);
        } catch (IOException exception) {
            Log.e("ZIKJsonUtils","Error converting JSON string to Map: " + exception.toString());
            map = Collections.emptyMap();
        }
        return map;
    }

    @NonNull
    public static List jsonStringToList(@NonNull final String string) {
        List list;
        try {
            list = ZIKJsonUtils.jsonAdapter(List.class).fromJson(string);
        } catch (IOException exception) {
            Log.e("ZIKJsonUtils","Error converting JSON string to List: " + exception.toString());
            list = Collections.emptyList();
        }
        return list;
    }

    @NonNull
    public static String listToJsonString(@NonNull final List list) {
        return ZIKJsonUtils.objectToJsonString(List.class,list);
    }
}
