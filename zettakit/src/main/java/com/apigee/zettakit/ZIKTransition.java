package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZIKTransition {
    private String href;
    private String name;
    private String method;
    private String type;
    private ArrayList<HashMap> fields;

    private void setHref(@NonNull final String href) { this.href = href; }
    @NonNull public String getHref() { return this.href; }

    private void setName(@NonNull final String name) { this.name = name; }
    @NonNull public String getName() { return this.name; }

    private void setMethod(@NonNull final String method) { this.method = method; }
    @NonNull public String getMethod() { return this.method; }

    private void setFields(@NonNull final ArrayList<HashMap> fields) { this.fields = fields; }
    @NonNull public ArrayList<HashMap> getFields() { return this.fields; }

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
