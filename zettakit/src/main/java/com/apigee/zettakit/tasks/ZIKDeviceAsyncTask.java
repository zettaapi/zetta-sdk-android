package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKDevice;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.interfaces.ZIKCallback;

import java.io.IOException;

public final class ZIKDeviceAsyncTask extends AsyncTask<Void,Void,Void> {
    @NonNull private final ZIKCallback<ZIKDevice> deviceCallback;
    @NonNull private final ZIKSession session;
    @NonNull private final ZIKDevice deviceToLoad;

    @Nullable private ZIKDevice loadedDevice;
    @Nullable private Exception exception;

    public ZIKDeviceAsyncTask(@NonNull final ZIKSession session, @NonNull final ZIKDevice deviceToLoad, @NonNull final ZIKCallback<ZIKDevice> deviceCallback) {
        this.session = session;
        this.deviceToLoad = deviceToLoad;
        this.deviceCallback = deviceCallback;
    }

    @Override
    protected Void doInBackground(final Void... v) {
        deviceToLoad.fetchSync(session, new ZIKCallback<ZIKDevice>() {
            @Override
            public void onSuccess(@NonNull final ZIKDevice device) {
                ZIKDeviceAsyncTask.this.loadedDevice = device;
            }
            @Override
            public void onFailure(@NonNull final Exception exception) {
                ZIKDeviceAsyncTask.this.exception = exception;
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(final Void aVoid) {
        if( exception != null ) {
            deviceCallback.onFailure(exception);
        } else if( loadedDevice != null ) {
            deviceCallback.onSuccess(this.loadedDevice);
        } else {
            // This realistically shouldn't happen.
            deviceCallback.onFailure(new IOException("Device loading failed without an exception."));
        }
    }
}
