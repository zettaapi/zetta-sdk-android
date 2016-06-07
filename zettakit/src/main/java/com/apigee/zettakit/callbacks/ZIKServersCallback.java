package com.apigee.zettakit.callbacks;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKServer;

import java.util.List;

public interface ZIKServersCallback {
    void onFinished(@NonNull final List<ZIKServer> servers);
}
