package com.apigee.zettakit.callbacks;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKDevice;

import java.io.IOException;
import java.util.List;

public interface ZIKDevicesCallback {
    void onSuccess(@NonNull final List<ZIKDevice> devices);
    void onFailure(@NonNull final IOException exception);
}
