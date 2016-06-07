package com.apigee.zettakit;

import android.support.annotation.NonNull;

import java.util.UUID;

public class ZIKDeviceId {
    @NonNull private transient UUID uuid;

    @NonNull public UUID getUuid() { return this.uuid; }
    @NonNull public String toString() { return this.uuid.toString(); }

    public ZIKDeviceId(@NonNull final String uuidString) {
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
}
