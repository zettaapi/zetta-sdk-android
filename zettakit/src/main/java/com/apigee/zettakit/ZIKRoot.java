package com.apigee.zettakit;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class ZIKRoot {

    @Nullable private ZIKLink href;
    @Nullable private ArrayList<ZIKLink> links;
    @Nullable private ArrayList<HashMap> actions;

    @Nullable
    @JsonProperty("links")
    public ArrayList<ZIKLink> getLinks() {
        return this.links;
    }
    @JsonProperty("links")
    private void setLinks(@Nullable final ArrayList<ZIKLink> links) {
        if( links != null ) {
            for( ZIKLink link : links ) {
                if( link.isSelf() ) {
                    this.href = link;
                }
            }
        }
        this.links = links;
    }

    @Nullable
    @JsonProperty("actions")
    public ArrayList<HashMap> getActions() {
        return this.actions;
    }
    @JsonProperty("actions")
    public void setActions(@Nullable final ArrayList<HashMap> actions) {
        this.actions = actions;
    }
}
