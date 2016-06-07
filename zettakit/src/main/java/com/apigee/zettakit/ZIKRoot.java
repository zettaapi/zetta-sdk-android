package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class ZIKRoot {
    @Nullable private ZIKLink href;
    @NonNull  private final List<ZIKLink> links;
    @NonNull  private final List<Map> actions;

    @Nullable public ZIKLink getHref() { return this.href; }
    @NonNull  public List<Map> getActions() { return this.actions; }
    @NonNull  public List<ZIKLink> getLinks() { return this.links; }

    public ZIKRoot(@NonNull final List<ZIKLink> links, @NonNull final List<Map> actions) {
        this.links = links;
        this.actions = actions;
        for( ZIKLink link : this.links ) {
            if( link.isSelf() ) {
                this.href = link;
                break;
            }
        }
    }
}
