package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.utils.ZIKJsonUtils;
import com.apigee.zettakit.utils.ZIKUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZIKRoot implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.href, flags);
        dest.writeTypedList(this.links);
        dest.writeString(ZIKJsonUtils.listToJsonString(this.actions));
    }

    @SuppressWarnings("unchecked")
    protected ZIKRoot(Parcel in) {
        this.href = in.readParcelable(ZIKLink.class.getClassLoader());
        this.links = in.createTypedArrayList(ZIKLink.CREATOR);
        List<Map<String,Object>> actions = new ArrayList<>();
        try {
            actions = ZIKJsonUtils.createObjectFromJson(List.class,in.readString());
        } catch( IOException ignored ) {}
        this.actions = actions;
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
