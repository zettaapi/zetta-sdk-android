package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ZIKTransition {
    private String href;
    private String name;
    private String method;
    private String type;
    private List<HashMap> fields;

    @NonNull public String getHref() { return this.href; }
    private void setHref(@NonNull final String href) { this.href = href; }

    @NonNull public String getName() { return this.name; }
    private void setName(@NonNull final String name) { this.name = name; }

    @NonNull public String getMethod() { return this.method; }
    private void setMethod(@NonNull final String method) { this.method = method; }

    @NonNull public List<HashMap> getFields() { return this.fields; }
    private void setFields(@NonNull final List<HashMap> fields) { this.fields = fields; }

    private void setType(@Nullable final String type) {
        if( type == null ) {
            this.type = "application/x-www-form-urlencoded";
        } else {
            this.type = type;
        }
    }
    @NonNull public String getType() { return this.type; }

    @NonNull
    public Request.Builder createRequestForTransition() {
        Request.Builder requestBuilder = new Request.Builder();
        return requestBuilder;
    }
}
