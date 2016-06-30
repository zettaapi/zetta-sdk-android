package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.interfaces.ZIKCallback;
import com.apigee.zettakit.interfaces.ZIKFetchable;
import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ZIKServer implements Parcelable, ZIKFetchable<ZIKServer> {
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

    @Override
    public String toString() { return ZIKJsonUtils.objectToJsonString(ZIKServer.class,this); }

    @NonNull
    public static ZIKServer fromString(@NonNull final String string) throws ZIKException {
        return ZIKJsonUtils.createObjectFromJson(ZIKServer.class,string);
    }

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
    public ZIKServer fetchSync() throws ZIKException {
        return this.fetchSync(ZIKSession.getSharedSession());
    }

    @Override
    public ZIKServer fetchSync(@NonNull ZIKSession session) throws ZIKException {
        try {
            return session.getServerWithLink(this.getSelfLink());
        } catch ( Exception exception ) {
            throw new ZIKException(exception);
        }
    }

    @Override
    public void fetchAsync(@NonNull ZIKCallback<ZIKServer> callback) {
        this.fetchAsync(ZIKSession.getSharedSession(),callback);
    }

    @Override
    public void fetchAsync(@NonNull final ZIKSession session, @NonNull final ZIKCallback<ZIKServer> serverCallback) {
        session.getServersAsync(Collections.singletonList(this.getSelfLink()), new ZIKCallback<List<ZIKServer>>() {
            @Override
            public void onSuccess(@NonNull List<ZIKServer> result) {
                if( !result.isEmpty() ){
                    serverCallback.onSuccess(result.get(0));
                } else {
                    serverCallback.onFailure(new ZIKException("Server fetch returned no server data."));
                }
            }
            @Override
            public void onFailure(@NonNull ZIKException exception) {
                serverCallback.onFailure(exception);
            }
        });
    }

    @NonNull
    public ZIKLink getSelfLink() {
        ZIKLink selfLink = new ZIKLink("",null,null);
        for( ZIKLink link : this.getLinks() ) {
            if( link.isSelf() ) {
                selfLink = link;
                break;
            }
        }
        return selfLink;
    }

    @Nullable
    public ZIKDevice getDeviceNamed(@NonNull final String name) {
        ZIKDevice deviceForName = null;
        for( ZIKDevice device : getDevices() ) {
            if( device.getName() != null && device.getName().equalsIgnoreCase(name) ) {
                deviceForName = device;
                break;
            }
        }
        return deviceForName;
    }

    @NonNull
    public List<ZIKLink> getAllDeviceSelfLinks() {
        ArrayList<ZIKLink> deviceLinks = new ArrayList<>();
        for( ZIKDevice serverDevice : this.getDevices() ) {
            for( ZIKLink deviceLink : serverDevice.getLinks() ) {
                if( deviceLink.isSelf() ) {
                    deviceLinks.add(deviceLink);
                }
            }
        }
        return deviceLinks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeString(ZIKJsonUtils.mapToJsonString(this.properties));
        dest.writeString(this.name);
        dest.writeParcelable(this.style, flags);
        dest.writeTypedList(this.devices);
        dest.writeTypedList(this.links);
        dest.writeTypedList(this.transitions);
    }

    @SuppressWarnings("unchecked")
    protected ZIKServer(@NonNull final Parcel in) {
        this.properties = ZIKJsonUtils.jsonStringToMap(in.readString());
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
