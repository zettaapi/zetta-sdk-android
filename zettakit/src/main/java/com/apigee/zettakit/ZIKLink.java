package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.util.List;

public class ZIKLink implements Parcelable {
    private static final String SELF = "self";

    @NonNull private final String href;
    @Nullable private final String title;
    @Nullable private final List<String> rel;

    @NonNull public String getHref() {
        return this.href;
    }
    @Nullable public String getTitle() { return this.title; }
    @Nullable public List<String> getRel() { return this.rel; }

    @Override
    public String toString() { return ZIKJsonUtils.objectToJsonString(ZIKLink.class,this); }

    @NonNull
    public static ZIKLink fromString(@NonNull final String string) throws ZIKException {
        return ZIKJsonUtils.createObjectFromJson(ZIKLink.class,string);
    }

    public ZIKLink(@NonNull final String href, @Nullable final String title, @Nullable final List<String> rel) {
        this.href = href;
        this.title = title;
        this.rel = rel;
    }

    public boolean hasRel(@NonNull final String rel) {
        boolean hasRel = false;
        if( this.rel != null ) {
            hasRel = this.rel.contains(rel);
        }
        return hasRel;
    }

    public boolean isSelf() {
        return this.hasRel(SELF);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeString(this.href);
        dest.writeString(this.title);
        dest.writeStringList(this.rel);
    }

    protected ZIKLink(@NonNull final Parcel in) {
        this.href = in.readString();
        this.title = in.readString();
        this.rel = in.createStringArrayList();
    }

    public static final Parcelable.Creator<ZIKLink> CREATOR = new Parcelable.Creator<ZIKLink>() {
        @Override
        public ZIKLink createFromParcel(Parcel source) {
            return new ZIKLink(source);
        }

        @Override
        public ZIKLink[] newArray(int size) {
            return new ZIKLink[size];
        }
    };
}
