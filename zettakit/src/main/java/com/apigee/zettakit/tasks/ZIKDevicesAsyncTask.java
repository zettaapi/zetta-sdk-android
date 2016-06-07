package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKDevice;
import com.apigee.zettakit.ZIKServer;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.callbacks.ZIKDevicesCallback;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ZIKDevicesAsyncTask extends AsyncTask<Void,Void,Void> {
    @NonNull private final ZIKDevicesCallback devicesCallback;
    @NonNull private final ZIKSession session;
    @NonNull private final ZIKServer server;

    @Nullable private List<ZIKDevice> devices;
    @Nullable private IOException exception;

    public ZIKDevicesAsyncTask(@NonNull final ZIKSession session, @NonNull final ZIKServer server, @NonNull final ZIKDevicesCallback devicesCallback) {
        this.session = session;
        this.server = server;
        this.devicesCallback = devicesCallback;
    }

    @Override
    protected Void doInBackground(final Void... v) {
        session.getDevicesSync(server, new ZIKDevicesCallback() {
            @Override
            public void onSuccess(@NonNull List<ZIKDevice> devices) {
                ZIKDevicesAsyncTask.this.devices = devices;
            }
            @Override
            public void onFailure(@NonNull IOException exception) {
                ZIKDevicesAsyncTask.this.exception = exception;
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if( exception != null ) {
            devicesCallback.onFailure(exception);
        } else {
            List<ZIKDevice> loadedDevices = this.devices;
            if( loadedDevices == null ) {
                loadedDevices = Collections.emptyList();
            }
            devicesCallback.onSuccess(loadedDevices);
        }
    }
}