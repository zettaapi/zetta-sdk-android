package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class ZIKServer {

    @NonNull  private final Map properties;
    @Nullable private final String name;
    @Nullable private final ZIKStyle style;

    @NonNull private final List<ZIKDevice> devices;
    @NonNull private final List<ZIKLink> links;
    @NonNull private final List<ZIKTransition> transitions;

    @NonNull  public Map getProperties() { return this.properties; }
    @Nullable public String getName() { return this.name; }
    @Nullable public ZIKStyle getStyle() { return this.style; }

    @NonNull public List<ZIKLink> getLinks() { return this.links; }
    @NonNull public List<ZIKTransition> getTransitions() { return this.transitions; }
    @NonNull public List<ZIKDevice> getDevices() { return this.devices; }

    public ZIKServer(@NonNull final Map properties, @NonNull final List<ZIKDevice> devices, @NonNull final List<ZIKLink> links, @NonNull final List<ZIKTransition> transitions, @Nullable final ZIKStyle style) {
        this.properties = properties;
        this.devices = devices;
        this.links = links;
        this.transitions = transitions;
        this.style = style;
        Object nameObject = properties.get("name");
        if( nameObject != null ) {
            this.name = nameObject.toString();
        } else {
            this.name = null;
        }
    }
}
