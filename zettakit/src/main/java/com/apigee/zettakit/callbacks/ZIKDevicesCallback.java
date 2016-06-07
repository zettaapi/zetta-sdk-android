package com.apigee.zettakit.callbacks;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKDevice;

import java.util.List;

public interface ZIKDevicesCallback {
    void onFinished(@NonNull final List<ZIKDevice> devices);
}
