package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.UUID;

public class ZIKDeviceId implements Parcelable {
    @NonNull private final transient UUID uuid;
    @NonNull private final String uuidString;

    @NonNull public UUID getUuid() { return this.uuid; }

    @Override
    public String toString() { return this.uuidString; }

    public ZIKDeviceId(@NonNull final String uuidString) {
        this.uuidString = uuidString;
        this.uuid = UUID.fromString(uuidString);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if( object == this ) {
            return true;
        }
        if( !(object instanceof ZIKDeviceId) ) {
            return false;
        }
        ZIKDeviceId deviceId = (ZIKDeviceId)object;
        return deviceId.uuid.equals(this.uuid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeString(this.uuidString);
    }

    protected ZIKDeviceId(@NonNull final Parcel in) {
        this.uuidString = in.readString();
        this.uuid = UUID.fromString(this.uuidString);
    }

    public static final Parcelable.Creator<ZIKDeviceId> CREATOR = new Parcelable.Creator<ZIKDeviceId>() {
        @Override
        public ZIKDeviceId createFromParcel(Parcel source) {
            return new ZIKDeviceId(source);
        }
        @Override
        public ZIKDeviceId[] newArray(int size) {
            return new ZIKDeviceId[size];
        }
    };
}
