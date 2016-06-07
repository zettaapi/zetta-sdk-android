package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public class ZIKLink {
    @NonNull private final String href;
    @Nullable private final String title;
    @Nullable private final List<String> rel;

    @NonNull public String getHref() {
        return this.href;
    }
    @Nullable public String getTitle() {
        return this.title;
    }
    @Nullable public List<String> getRel() { return this.rel; }

    public ZIKLink(@NonNull final String href, @Nullable final String title, @Nullable final List<String> rel) {
        this.href = href;
        this.title = title;
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
