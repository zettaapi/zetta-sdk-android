package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKDevice;
import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKServer;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.callbacks.ZIKDevicesCallback;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ZIKDevicesAsyncTask extends AsyncTask<Void,Void,Void> {
    @NonNull private final ZIKDevicesCallback devicesCallback;
    @NonNull public final ZIKSession session;
    @NonNull public final ZIKServer server;
    @Nullable public List<ZIKDevice> devices;

    public ZIKDevicesAsyncTask(@NonNull final ZIKSession session, @NonNull final ZIKServer server, @NonNull final ZIKDevicesCallback devicesCallback) {
        this.session = session;
        this.server = server;
        this.devicesCallback = devicesCallback;
    }

    @Override @NonNull
    protected Void doInBackground(final Void... v) {
        final ArrayList<ZIKLink> devicesLinks = new ArrayList<>();
        List<ZIKDevice> serverDevices = server.getDevices();
        if( serverDevices != null && !serverDevices.isEmpty() ) {
            for( ZIKDevice device : serverDevices ) {
                List<ZIKLink> deviceLinks = device.getLinks();
                if( deviceLinks != null && !deviceLinks.isEmpty() ) {
                    for( ZIKLink deviceLink : deviceLinks ) {
                        if( deviceLink.isSelf() ) {
                            devicesLinks.add(deviceLink);
                        }
                    }
                }
            }
        }
        if( !devicesLinks.isEmpty() ) {
            ArrayList<ZIKDevice> loadedDevices = new ArrayList<>();
            for( ZIKLink deviceLink : devicesLinks ) {
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url(deviceLink.getHref());
                session.addHeadersToRequest(requestBuilder);
                Request request = requestBuilder.get().build();
                try {
                    Response response = ZIKSession.httpClient.newCall(request).execute();
                    if( response.isSuccessful() ) {
                        JsonNode jsonNode = ZIKSession.jsonMapper.readTree(response.body().string());
                        if( jsonNode != null ) {
                            JsonParser jsonParser = jsonNode.traverse();
                            ZIKDevice device = ZIKSession.jsonMapper.readValue(jsonParser,ZIKDevice.class);
                            loadedDevices.add(device);
                        }
                    }
                } catch( IOException ignored ) { }
            }
            this.devices = loadedDevices;
        }
        return null;
    }

    @Override
    protected void onPostExecute(@NonNull final Void aVoid) {
        devicesCallback.onFinished(devices);
    }
}