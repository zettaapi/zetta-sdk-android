package com.apigee.zettakit.interfaces;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKException;
import com.apigee.zettakit.ZIKSession;

public interface ZIKFetchable<T> {
    T fetchSync() throws ZIKException;
    T fetchSync(@NonNull final ZIKSession session) throws ZIKException;
    void fetchAsync(@NonNull final ZIKCallback<T> callback);
    void fetchAsync(@NonNull final ZIKSession session, @NonNull final ZIKCallback<T> callback);
}
