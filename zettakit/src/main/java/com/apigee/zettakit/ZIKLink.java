package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ZIKLink {

    @Nullable private String href;
    @Nullable private String title;
    @Nullable private ArrayList<String> rel;

    @Nullable
    @JsonProperty("href")
    public String getHref() {
        return this.href;
    }
    @JsonProperty("href")
    private void setHref(@Nullable String href) {
        this.href = href;
    }

    @Nullable
    @JsonProperty("title")
    public String getTitle() {
        return this.title;
    }
    @JsonProperty("title")
    private void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nullable
    @JsonProperty("rel")
    public ArrayList<String> getRel() {
        return this.rel;
    }
    @JsonProperty("rel")
    private void setRel(@Nullable ArrayList<String> rel) {
        this.rel = rel;
    }

    public boolean hasRel(@NonNull final String rel) {
        boolean hasRel = false;
        if( this.rel != null ) {
            hasRel = this.rel.contains(rel);
        }
        return hasRel;
    }

    public boolean isSelf() {
        return this.hasRel("self");
    }
}
