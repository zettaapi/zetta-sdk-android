package com.apigee.zettakit.callbacks;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKRoot;

public interface ZIKRootCallback {
    void onSuccess(@NonNull final ZIKRoot root);
    void onFailure(@NonNull final Exception exception);
}
