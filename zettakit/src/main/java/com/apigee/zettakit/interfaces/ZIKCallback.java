package com.apigee.zettakit.interfaces;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKException;

public interface ZIKCallback<T> {
    void onSuccess(@NonNull final T result);
    void onFailure(@NonNull final ZIKException exception);
}
