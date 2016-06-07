package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKDevice;
import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKServer;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.callbacks.ZIKDevicesCallback;
import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ZIKDevicesAsyncTask extends AsyncTask<Void,Void,Void> {
    @NonNull private final ZIKDevicesCallback devicesCallback;
    @NonNull private final ZIKSession session;
    @NonNull private final ZIKServer server;
    @NonNull private final ArrayList<ZIKDevice> devices = new ArrayList<ZIKDevice>();

    public ZIKDevicesAsyncTask(@NonNull final ZIKSession session, @NonNull final ZIKServer server, @NonNull final ZIKDevicesCallback devicesCallback) {
        this.session = session;
        this.server = server;
        this.devicesCallback = devicesCallback;
    }

    @Override @NonNull
    protected Void doInBackground(final Void... v) {
        final ArrayList<ZIKLink> devicesLinks = new ArrayList<ZIKLink>();
        List<ZIKDevice> serverDevices = server.getDevices();
        if( !serverDevices.isEmpty() ) {
            for( ZIKDevice device : serverDevices ) {
                List<ZIKLink> deviceLinks = device.getLinks();
                if( !deviceLinks.isEmpty() ) {
                    for( ZIKLink deviceLink : deviceLinks ) {
                        if( deviceLink.isSelf() ) {
                            devicesLinks.add(deviceLink);
                        }
                    }
                }
            }
        }
        if( !devicesLinks.isEmpty() ) {
            for( ZIKLink deviceLink : devicesLinks ) {
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url(deviceLink.getHref());
                session.addHeadersToRequest(requestBuilder);
                Request request = requestBuilder.get().build();
                try {
                    Response response = ZIKSession.httpClient.newCall(request).execute();
                    if( response.isSuccessful() ) {
                        ZIKDevice device = ZIKJsonUtils.createObjectFromJson(ZIKDevice.class,response.body().string());
                        this.devices.add(device);
                    }
                } catch( IOException ignored ) { }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(@NonNull final Void aVoid) {
        devicesCallback.onFinished(devices);
    }
}