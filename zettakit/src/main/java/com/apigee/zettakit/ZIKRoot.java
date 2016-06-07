package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.utils.ZIKUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZIKRoot {
    private static final String SERVER_REL = ZIKUtils.generateRelForString("server");

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

    @NonNull
    public List<ZIKLink> getAllServerLinks() {
        ArrayList<ZIKLink> serverLinks = new ArrayList<>();
        for( ZIKLink link : this.links ) {
            if( link.hasRel(SERVER_REL) ) {
                serverLinks.add(link);
            }
        }
        return serverLinks;
    }
}
