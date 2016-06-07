package com.apigee.zettakit.callbacks;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKServer;

import java.io.IOException;
import java.util.List;

public interface ZIKServersCallback {
    void onSuccess(@NonNull final List<ZIKServer> servers);
    void onFailure(@NonNull final IOException exception);
}
