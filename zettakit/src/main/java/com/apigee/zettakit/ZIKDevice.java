package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZIKDevice implements Parcelable {
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String STATE = "state";
    private static final String MONITOR = "monitor";

    @NonNull  private final ZIKDeviceId deviceId;
    @NonNull  private final String type;
    @Nullable private final String name;
    @Nullable private final String state;
    @Nullable private final ZIKStyle style;

    @NonNull  private final Map<String,Object> properties;
    @NonNull  private final List<ZIKLink> links;
    @NonNull  private final List<ZIKLink> streamLinks;
    @NonNull  private final List<ZIKTransition> transitions;

    @NonNull  public ZIKDeviceId getDeviceId() { return this.deviceId; }
    @NonNull  public String getType() { return this.type; }
    @Nullable public String getName() { return this.name; }
    @Nullable public String getState() { return this.state; }
    @Nullable public ZIKStyle getStyle() { return this.style; }

    @NonNull public Map<String,Object> getProperties() { return this.properties; }
    @NonNull public List<ZIKLink> getLinks() { return this.links; }
    @NonNull public List<ZIKTransition> getTransitions() { return this.transitions; }

    public ZIKDevice(@NonNull final Map<String,Object> properties, @NonNull final List<ZIKLink> links, @NonNull final List<ZIKTransition> transitions, @Nullable final ZIKStyle style) {
        this.properties = properties;
        this.links = links;
        this.style = style;
        this.transitions = transitions;
        this.deviceId = new ZIKDeviceId(properties.get(ID).toString());
        this.type = properties.get(TYPE).toString();

        Object nameObject = properties.get(NAME);
        if( nameObject != null ) {
            this.name = nameObject.toString();
        } else {
            this.name = null;
        }
        Object stateObject = properties.get(STATE);
        if( stateObject != null ) {
            this.state = stateObject.toString();
        } else {
            this.state = null;
        }

        if( !links.isEmpty() ) {
            ArrayList<ZIKLink> streamLinks = new ArrayList<ZIKLink>();
            for( ZIKLink link : links ) {
                if( link.hasRel(MONITOR) ) {
                    streamLinks.add(link);
                }
            }
            this.streamLinks = streamLinks;
        } else {
            this.streamLinks = Collections.emptyList();
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
        ArrayList<ZIKStream> streams = new ArrayList<ZIKStream>();
        if ( !streamLinks.isEmpty() ) {
            for( ZIKLink link : streamLinks ) {
                streams.add(new ZIKStream(link));
            }
        }
        return streams;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.deviceId, flags);
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeString(this.state);
        dest.writeParcelable(this.style, flags);
        dest.writeString(ZIKJsonUtils.mapToJsonString(this.properties));
        dest.writeTypedList(this.links);
        dest.writeTypedList(this.streamLinks);
        dest.writeTypedList(this.transitions);
    }

    @SuppressWarnings("unchecked")
    protected ZIKDevice(Parcel in) {
        this.deviceId = in.readParcelable(ZIKDeviceId.class.getClassLoader());
        this.type = in.readString();
        this.name = in.readString();
        this.state = in.readString();
        this.style = in.readParcelable(ZIKStyle.class.getClassLoader());
        Map<String,Object> properties = new HashMap<>();
        try {
            properties = (Map<String,Object>)ZIKJsonUtils.createObjectFromJson(Map.class,in.readString());
        } catch (IOException ignored) { }
        this.properties = properties;
        this.links = in.createTypedArrayList(ZIKLink.CREATOR);
        this.streamLinks = in.createTypedArrayList(ZIKLink.CREATOR);
        this.transitions = in.createTypedArrayList(ZIKTransition.CREATOR);
    }

    public static final Parcelable.Creator<ZIKDevice> CREATOR = new Parcelable.Creator<ZIKDevice>() {
        @Override
        public ZIKDevice createFromParcel(Parcel source) {
            return new ZIKDevice(source);
        }

        @Override
        public ZIKDevice[] newArray(int size) {
            return new ZIKDevice[size];
        }
    };
}
