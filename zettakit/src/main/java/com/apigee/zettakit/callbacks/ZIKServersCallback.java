package com.apigee.zettakit.callbacks;

import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKServer;

import java.util.List;

public interface ZIKServersCallback {
    void onFinished(@Nullable final List<ZIKServer> servers);
}
