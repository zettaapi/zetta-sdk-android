package com.apigee.zettakit.interfaces;

import android.support.annotation.NonNull;

public interface ZIKCallback<T> {
    void onSuccess(@NonNull final T result);
    void onFailure(@NonNull final Exception exception);
}
