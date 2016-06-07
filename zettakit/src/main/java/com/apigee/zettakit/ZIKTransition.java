package com.apigee.zettakit;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class ZIKTransition {
    @NonNull private final String href;
    @NonNull private final String name;
    @NonNull private final String method;
    @NonNull private final String type;
    @NonNull private final List<Map> fields;

    @NonNull public String getHref() { return this.href; }
    @NonNull public String getName() { return this.name; }
    @NonNull public String getMethod() { return this.method; }
    @NonNull public List<Map> getFields() { return this.fields; }
    @NonNull public String getType() { return this.type; }

    public ZIKTransition(@NonNull final String href,
                         @NonNull final String name,
                         @NonNull final String method,
                         @NonNull final String type,
                         @NonNull final List<Map> fields) {
        this.href = href;
        this.name = name;
        this.method = method;
        this.type = type;
        this.fields = fields;
    }

    @NonNull
    public Request.Builder createRequestForTransition() {
        Request.Builder requestBuilder = new Request.Builder();
        return requestBuilder;
    }
}
