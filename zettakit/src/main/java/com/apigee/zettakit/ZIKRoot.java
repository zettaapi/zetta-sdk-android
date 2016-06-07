package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class ZIKRoot {
    @Nullable private final ZIKLink href;
    @NonNull  private final List<ZIKLink> links;
    @NonNull  private final List<Map<String,Object>> actions;

    @Nullable public ZIKLink getHref() { return this.href; }
    @NonNull  public List<Map<String,Object>> getActions() { return this.actions; }
    @NonNull  public List<ZIKLink> getLinks() { return this.links; }

    public ZIKRoot(@NonNull final List<ZIKLink> links, @NonNull final List<Map<String,Object>> actions) {
        this.links = links;
        this.actions = actions;
        ZIKLink hrefLink = null;
        for( ZIKLink link : this.links ) {
            if( link.isSelf() ) {
                hrefLink = link;
                break;
            }
        }
        this.href = hrefLink;
    }
}
