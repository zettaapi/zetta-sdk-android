package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

import okhttp3.HttpUrl;

public class ZIKSession {

    @Nullable public HttpUrl apiEndpoint;
    @NonNull private HashMap<String,Object> headers = new HashMap<>();

    @NonNull private static ZIKSession sharedSession = new ZIKSession();

    @NonNull
    public static ZIKSession getSharedSession() {
        return ZIKSession.sharedSession;
    }

    public ZIKRoot getRoot(@NonNull final HttpUrl url) {
        return null;
    }

    public void setGlobalHeaders(@NonNull final HashMap<String,Object> headers) {
        this.headers = headers;
    }

    public void setHeader(@NonNull final String key, @NonNull final String value) {
        this.headers.put(key,value);
    }

    public void unsetHeader(@NonNull final String key) {
        this.headers.remove(key);
    }
}
