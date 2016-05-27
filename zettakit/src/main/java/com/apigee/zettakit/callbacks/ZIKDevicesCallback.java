package com.apigee.zettakit.callbacks;

import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKDevice;

import java.util.List;

public interface ZIKDevicesCallback {
    void onFinished(@Nullable final List<ZIKDevice> devices);
}
