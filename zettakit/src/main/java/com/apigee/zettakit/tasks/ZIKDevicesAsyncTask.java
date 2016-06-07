package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKDevice;
import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKServer;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.callbacks.ZIKDevicesCallback;
import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ZIKDevicesAsyncTask extends AsyncTask<Void,Void,List<ZIKDevice>> {
    @NonNull private final ZIKDevicesCallback devicesCallback;
    @NonNull private final ZIKSession session;
    @NonNull private final ZIKServer server;

    public ZIKDevicesAsyncTask(@NonNull final ZIKSession session, @NonNull final ZIKServer server, @NonNull final ZIKDevicesCallback devicesCallback) {
        this.session = session;
        this.server = server;
        this.devicesCallback = devicesCallback;
    }

    @Override @Nullable
    protected List<ZIKDevice> doInBackground(final Void... v) {
        ArrayList<ZIKDevice> loadedDevices = null;
        List<ZIKDevice> serverDevices = server.getDevices();
        if( ! serverDevices.isEmpty() ) {
            loadedDevices = new ArrayList<>();
            for( ZIKDevice serverDevice : serverDevices ) {
                for( ZIKLink deviceLink : serverDevice.getLinks() ) {
                    if( deviceLink.isSelf() ) {
                        Request.Builder requestBuilder = new Request.Builder();
                        requestBuilder.url(deviceLink.getHref());
                        session.addHeadersToRequest(requestBuilder);
                        Request request = requestBuilder.get().build();
                        try {
                            Response response = ZIKSession.httpClient.newCall(request).execute();
                            if( response.isSuccessful() ) {
                                loadedDevices.add(ZIKJsonUtils.createObjectFromJson(ZIKDevice.class,response.body().string()));
                            }
                        } catch( IOException ignored ) { }
                    }
                }
            }
        }
        return loadedDevices;
    }

    @Override
    protected void onPostExecute(@Nullable final List<ZIKDevice> devices) {
        List<ZIKDevice> loadedDevices = devices;
        if( loadedDevices == null ) {
            loadedDevices = Collections.emptyList();
        }
        devicesCallback.onFinished(loadedDevices);
    }
}