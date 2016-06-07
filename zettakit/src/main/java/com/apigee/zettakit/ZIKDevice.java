package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZIKDevice {
    @NonNull private final ZIKDeviceId deviceId;
    @NonNull private final String type;
    @Nullable private final String name;
    @Nullable private final String state;

    @NonNull private final Map properties;
    @Nullable private final ZIKStyle style;
    @NonNull private final List<ZIKLink> links;
    @NonNull private final List<ZIKLink> streamLinks;
    @NonNull private final List<ZIKTransition> transitions;

    @NonNull public ZIKDeviceId getDeviceId() { return this.deviceId; }
    @NonNull public String getType() { return this.type; }
    @Nullable public String getName() { return this.name; }
    @Nullable public String getState() { return this.state; }
    @Nullable public ZIKStyle getStyle() { return this.style; }

    @NonNull public Map getProperties() { return this.properties; }
    @NonNull public List<ZIKLink> getLinks() { return this.links; }
    @NonNull public List<ZIKTransition> getTransitions() { return this.transitions; }

    public ZIKDevice(@NonNull final Map properties, @NonNull final List<ZIKLink> links, @NonNull final List<ZIKTransition> transitions, @Nullable final ZIKStyle style) {
        this.properties = properties;
        this.links = links;
        this.style = style;
        this.transitions = transitions;

        this.deviceId = new ZIKDeviceId(properties.get("id").toString());
        this.type = properties.get("type").toString();
        Object nameObject = properties.get("name");
        if( nameObject != null ) {
            this.name = nameObject.toString();
        } else {
            this.name = null;
        }
        Object stateObject = properties.get("state");
        if( stateObject != null ) {
            this.state = stateObject.toString();
        } else {
            this.state = null;
        }

        if( !links.isEmpty() ) {
            ArrayList<ZIKLink> streamLinks = new ArrayList<>();
            for( ZIKLink link : links ) {
                if( link.hasRel("monitor") ) {
                    streamLinks.add(link);
                }
            }
            this.streamLinks = streamLinks;
        } else {
            this.streamLinks = new ArrayList<>();
        }
    }

    @Nullable
    public ZIKStream stream(@NonNull final String name) {
        ZIKStream stream = null;
        if( !streamLinks.isEmpty() ) {
            for( ZIKLink streamLink : streamLinks ) {
                String streamLinkTitle = streamLink.getTitle();
                if( streamLinkTitle != null && streamLinkTitle.equalsIgnoreCase(name) ) {
                    stream = new ZIKStream(streamLink);
                }
            }
        }
        return stream;
    }

    @NonNull
    public List<ZIKStream> getAllStreams() {
        ArrayList<ZIKStream> streams = new ArrayList<>();
        if ( !streamLinks.isEmpty() ) {
            for( ZIKLink link : streamLinks ) {
                streams.add(new ZIKStream(link));
            }
        }
        return streams;
    }
}
