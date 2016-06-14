package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.apigee.zettakit.interfaces.ZIKCallback;
import com.apigee.zettakit.interfaces.ZIKFetchable;
import com.apigee.zettakit.utils.ZIKJsonUtils;
import com.apigee.zettakit.utils.ZIKUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZIKRoot implements Parcelable, ZIKFetchable<ZIKRoot> {
    private static final String SERVER_REL = ZIKUtils.generateRelForString("server");

    @NonNull private final ZIKLink selfLink;
    @NonNull  private final List<ZIKLink> links;
    @NonNull  private final List<Map<String,Object>> actions;

    @NonNull public ZIKLink getSelfLink() { return this.selfLink; }
    @NonNull  public List<Map<String,Object>> getActions() { return this.actions; }
    @NonNull  public List<ZIKLink> getLinks() { return this.links; }

    @Override
    public String toString() { return ZIKJsonUtils.objectToJsonString(ZIKRoot.class,this); }

    @NonNull
    public static ZIKRoot fromString(@NonNull final String string) throws ZIKException {
        return ZIKJsonUtils.createObjectFromJson(ZIKRoot.class,string);
    }

    public ZIKRoot(@NonNull final List<ZIKLink> links, @NonNull final List<Map<String,Object>> actions) {
        this.links = links;
        this.actions = actions;
        ZIKLink selfLink = new ZIKLink("",null,null);
        for( ZIKLink link : this.links ) {
            if( link.isSelf() ) {
                selfLink = link;
                break;
            }
        }
        this.selfLink = selfLink;
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

    @Override
    public ZIKRoot fetchSync() throws ZIKException {
        return this.fetchSync(ZIKSession.getSharedSession());
    }

    @Override
    public ZIKRoot fetchSync(@NonNull ZIKSession session) throws ZIKException {
        try {
            return session.getRootWithURL(this.selfLink.getHref());
        } catch (Exception exception) {
            throw new ZIKException(exception);
        }
    }

    @Override
    public void fetchAsync(@NonNull ZIKCallback<ZIKRoot> callback) {
        this.fetchAsync(ZIKSession.getSharedSession(),callback);
    }

    @Override
    public void fetchAsync(@NonNull ZIKSession session, @NonNull ZIKCallback<ZIKRoot> callback) {
        session.getRootAsync(this.selfLink.getHref(),callback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeParcelable(this.selfLink, flags);
        dest.writeTypedList(this.links);
        dest.writeString(ZIKJsonUtils.listToJsonString(this.actions));
    }

    @SuppressWarnings("unchecked")
    protected ZIKRoot(@NonNull final Parcel in) {
        this.selfLink = in.readParcelable(ZIKLink.class.getClassLoader());
        this.links = in.createTypedArrayList(ZIKLink.CREATOR);
        this.actions = ZIKJsonUtils.jsonStringToList(in.readString());
    }

    public static final Parcelable.Creator<ZIKRoot> CREATOR = new Parcelable.Creator<ZIKRoot>() {
        @Override
        public ZIKRoot createFromParcel(Parcel source) {
            return new ZIKRoot(source);
        }

        @Override
        public ZIKRoot[] newArray(int size) {
            return new ZIKRoot[size];
        }
    };
}
