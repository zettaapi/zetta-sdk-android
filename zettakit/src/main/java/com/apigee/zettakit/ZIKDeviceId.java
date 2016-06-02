package com.apigee.zettakit;

import android.support.annotation.NonNull;

import java.util.UUID;

public class ZIKDeviceId {
    private final UUID uuid;

    public ZIKDeviceId(@NonNull final UUID uuid) {
        this.uuid = uuid;
    }

    public String toString() {
        return this.uuid.toString();
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object object) {
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
