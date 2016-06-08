package com.apigee.zettakit.utils;

import android.support.annotation.NonNull;

import com.apigee.zettakit.jsonHelpers.ZIKDeviceJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKLinkJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKRootJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKServerJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKStreamEntryJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKStyleJsonAdapter;
import com.apigee.zettakit.jsonHelpers.ZIKTransitionJsonAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class ZIKJsonUtils {
    private static final Moshi moshi;
    static {
        Moshi.Builder moshiBuilder = new Moshi.Builder();
        moshiBuilder.add(new ZIKDeviceJsonAdapter());
        moshiBuilder.add(new ZIKLinkJsonAdapter());
        moshiBuilder.add(new ZIKRootJsonAdapter());
        moshiBuilder.add(new ZIKServerJsonAdapter());
        moshiBuilder.add(new ZIKStreamEntryJsonAdapter());
        moshiBuilder.add(new ZIKStyleJsonAdapter());
        moshiBuilder.add(new ZIKTransitionJsonAdapter());
        moshi = moshiBuilder.build();
    }

    public static <T> JsonAdapter<T> jsonAdapter(@NonNull final Class<T> objectClass) {
        return moshi.adapter(objectClass);
    }

    @NonNull
    public static <T> T createObjectFromJson(@NonNull final Class<T> objectClass, @NonNull final String jsonString) throws IOException {
        return ZIKJsonUtils.jsonAdapter(objectClass).fromJson(jsonString);
    }

    @NonNull
    public static <T> T convertJsonMapToObject(@NonNull final Class<T> objectClass, @NonNull final Map jsonMap) throws IOException {
        return ZIKJsonUtils.createObjectFromJson(objectClass,ZIKJsonUtils.mapToJsonString(jsonMap));
    }

    @NonNull
    public static String mapToJsonString(@NonNull final Map jsonMap) {
        return ZIKJsonUtils.jsonAdapter(Map.class).toJson(jsonMap);
    }

    @NonNull
    public static String listToJsonString(@NonNull final List list) {
        return ZIKJsonUtils.jsonAdapter(List.class).toJson(list);
    }
}
