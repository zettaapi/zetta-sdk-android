package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZIKServer implements Parcelable {
    private static final String NAME = "name";

    @NonNull  private final Map<String,Object> properties;
    @Nullable private final String name;
    @Nullable private final ZIKStyle style;

    @NonNull private final List<ZIKDevice> devices;
    @NonNull private final List<ZIKLink> links;
    @NonNull private final List<ZIKTransition> transitions;

    @NonNull  public Map<String,Object> getProperties() { return this.properties; }
    @Nullable public String getName() { return this.name; }
    @Nullable public ZIKStyle getStyle() { return this.style; }

    @NonNull public List<ZIKLink> getLinks() { return this.links; }
    @NonNull public List<ZIKTransition> getTransitions() { return this.transitions; }
    @NonNull public List<ZIKDevice> getDevices() { return this.devices; }

    public ZIKServer(@NonNull final Map<String,Object> properties, @NonNull final List<ZIKDevice> devices, @NonNull final List<ZIKLink> links, @NonNull final List<ZIKTransition> transitions, @Nullable final ZIKStyle style) {
        this.properties = properties;
        this.devices = devices;
        this.links = links;
        this.transitions = transitions;
        this.style = style;
        Object nameObject = properties.get(NAME);
        if( nameObject != null ) {
            this.name = nameObject.toString();
        } else {
            this.name = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ZIKJsonUtils.mapToJsonString(this.properties));
        dest.writeString(this.name);
        dest.writeParcelable(this.style, flags);
        dest.writeTypedList(this.devices);
        dest.writeTypedList(this.links);
        dest.writeTypedList(this.transitions);
    }

    @SuppressWarnings("unchecked")
    protected ZIKServer(Parcel in) {
        Map<String,Object> properties = new HashMap<String, Object>();
        try {
            properties = ZIKJsonUtils.createObjectFromJson(Map.class,in.readString());
        } catch( IOException ignored ) {}
        this.properties = properties;
        this.name = in.readString();
        this.style = in.readParcelable(ZIKStyle.class.getClassLoader());
        this.devices = in.createTypedArrayList(ZIKDevice.CREATOR);
        this.links = in.createTypedArrayList(ZIKLink.CREATOR);
        this.transitions = in.createTypedArrayList(ZIKTransition.CREATOR);
    }

    public static final Parcelable.Creator<ZIKServer> CREATOR = new Parcelable.Creator<ZIKServer>() {
        @Override
        public ZIKServer createFromParcel(Parcel source) {
            return new ZIKServer(source);
        }

        @Override
        public ZIKServer[] newArray(int size) {
            return new ZIKServer[size];
        }
    };
}
