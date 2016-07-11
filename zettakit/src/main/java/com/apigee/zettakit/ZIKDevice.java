package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.interfaces.ZIKCallback;
import com.apigee.zettakit.interfaces.ZIKFetchable;
import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ZIKDevice implements Parcelable, ZIKFetchable<ZIKDevice> {
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String STATE = "state";
    private static final String MONITOR = "monitor";

    @NonNull  private final ZIKDeviceId deviceId;
    @NonNull  private final String type;
    @NonNull private final String name;
    @NonNull private final String state;
    @Nullable private final ZIKStyle style;

    @NonNull  private final Map<String,Object> properties;
    @NonNull  private final List<ZIKLink> links;
    @NonNull  private final List<ZIKLink> streamLinks;
    @NonNull  private final List<ZIKTransition> transitions;

    @NonNull  public ZIKDeviceId getDeviceId() { return this.deviceId; }
    @NonNull  public String getType() { return this.type; }
    @NonNull public String getName() { return this.name; }
    @NonNull public String getState() { return this.state; }
    @Nullable public ZIKStyle getStyle() { return this.style; }

    @NonNull public Map<String,Object> getProperties() { return this.properties; }
    @NonNull public List<ZIKLink> getLinks() { return this.links; }
    @NonNull public List<ZIKTransition> getTransitions() { return this.transitions; }

    @Override
    public String toString() { return ZIKJsonUtils.objectToJsonString(ZIKDevice.class,this); }

    @NonNull
    public static ZIKDevice fromString(@NonNull final String string) throws ZIKException {
        return ZIKJsonUtils.createObjectFromJson(ZIKDevice.class,string);
    }

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
            this.name = "";
        }
        Object stateObject = properties.get(STATE);
        if( stateObject != null ) {
            this.state = stateObject.toString();
        } else {
            this.state = "";
        }

        if( !links.isEmpty() ) {
            ArrayList<ZIKLink> streamLinks = new ArrayList<>();
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

    public void transition(@NonNull final String transitionName, @NonNull final ZIKCallback<ZIKDevice> deviceCallback) {
        this.transition(transitionName,new HashMap<String, Object>(),deviceCallback);
    }

    public void transition(@NonNull final String transitionName, @NonNull final Map<String,Object> arguments, @NonNull final ZIKCallback<ZIKDevice> deviceCallback) {
        ZIKTransition transition = null;
        for( ZIKTransition deviceTransition : this.getTransitions() ) {
            if( deviceTransition.getName().equalsIgnoreCase(transitionName) ) {
                transition = deviceTransition;
                break;
            }
        }
        if( transition != null ) {
            this.transition(transition,arguments,deviceCallback);
        } else {
            deviceCallback.onFailure(new ZIKException("No transition found with name: " + transitionName));
        }
    }

    public void transition(@NonNull final ZIKTransition transition, @NonNull final Map<String,Object> arguments, @NonNull final ZIKCallback<ZIKDevice> deviceCallback) {
        Request request;
        try {
            request = transition.requestForTransition(arguments);
        } catch (IllegalArgumentException exception) {
            deviceCallback.onFailure(new ZIKException(exception));
            return;
        }

        ZIKSession.httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deviceCallback.onFailure(new ZIKException(e));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseString = response.body().string();
                    ZIKDevice device = ZIKDevice.fromString(responseString);
                    deviceCallback.onSuccess(device);
                } catch (ZIKException exception) {
                    deviceCallback.onFailure(exception);
                }
            }
        });
    }

    @Override
    public ZIKDevice fetchSync() throws ZIKException {
        return this.fetchSync(ZIKSession.getSharedSession());
    }

    @Override
    public ZIKDevice fetchSync(@NonNull ZIKSession session) throws ZIKException {
        try {
            return session.getDeviceWithLink(this.getSelfLink());
        } catch (Exception exception) {
            throw new ZIKException(exception);
        }
    }

    @Override
    public void fetchAsync(@NonNull final ZIKCallback<ZIKDevice> deviceCallback) {
        this.fetchAsync(ZIKSession.getSharedSession(),deviceCallback);
    }

    @Override
    public void fetchAsync(@NonNull final ZIKSession session, @NonNull final ZIKCallback<ZIKDevice> deviceCallback) {
        session.getDevicesAsync(Collections.singletonList(this.getSelfLink()), new ZIKCallback<List<ZIKDevice>>() {
            @Override
            public void onSuccess(@NonNull List<ZIKDevice> result) {
                if( ! result.isEmpty() ) {
                    deviceCallback.onSuccess(result.get(0));
                } else {
                    deviceCallback.onFailure(new ZIKException("Device fetch returned no device data."));
                }
            }
            @Override
            public void onFailure(@NonNull ZIKException exception) {
                deviceCallback.onFailure(exception);
            }
        });
    }

    @NonNull
    public ZIKDevice refreshWithLogEntry(@NonNull final ZIKLogStreamEntry logStreamEntry) {
        return new ZIKDevice(logStreamEntry.getProperties(),this.getLinks(),logStreamEntry.getTransitions(),this.getStyle());
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

    @Nullable
    public ZIKStream getStateStream() {
        return this.stream("state");
    }

    @Nullable
    public ZIKStream getLogStream() {
        return this.stream("logs");
    }

    @NonNull
    public List<ZIKStream> getPropertyStreams() {
        ArrayList<ZIKStream> streams = new ArrayList<>();
        if ( !streamLinks.isEmpty() ) {
            for( ZIKLink link : streamLinks ) {
                String streamLinkTitle = link.getTitle();
                if( streamLinkTitle != null && !streamLinkTitle.equalsIgnoreCase("logs") && !streamLinkTitle.equalsIgnoreCase("state") ) {
                    streams.add(new ZIKStream(link));
                }
            }
        }
        return streams;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
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
    protected ZIKDevice(@NonNull final Parcel in) {
        this.deviceId = in.readParcelable(ZIKDeviceId.class.getClassLoader());
        this.type = in.readString();
        this.name = in.readString();
        this.state = in.readString();
        this.style = in.readParcelable(ZIKStyle.class.getClassLoader());
        this.properties = ZIKJsonUtils.jsonStringToMap(in.readString());
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
