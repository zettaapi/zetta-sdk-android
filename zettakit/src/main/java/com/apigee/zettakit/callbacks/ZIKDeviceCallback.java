package com.apigee.zettakit.callbacks;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKDevice;

import java.io.IOException;

public interface ZIKDeviceCallback {
    void onSuccess(@NonNull final ZIKDevice device);
    void onFailure(@NonNull final IOException exception);
}
