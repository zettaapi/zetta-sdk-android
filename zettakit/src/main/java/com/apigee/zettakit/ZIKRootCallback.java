package com.apigee.zettakit;

import android.support.annotation.NonNull;

public interface ZIKRootCallback {
    void onSuccess(@NonNull final ZIKRoot root);
    void onError(@NonNull final String error);
}
