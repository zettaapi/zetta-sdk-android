package com.apigee.zettakit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.interfaces.ZIKCallback;
import com.apigee.zettakit.tasks.ZIKDevicesAsyncTask;
import com.apigee.zettakit.tasks.ZIKRootAsyncTask;
import com.apigee.zettakit.tasks.ZIKServersAsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZIKSession {

    @Nullable private static Context appContext;

    @NonNull protected static final OkHttpClient httpClient = new OkHttpClient();
    @NonNull private static final ZIKSession sharedSession = new ZIKSession();
    @NonNull public static ZIKSession getSharedSession() {
        return ZIKSession.sharedSession;
    }
    @NonNull private HashMap<String,Object> headers = new HashMap<>();

    public static void init(@NonNull final Context context) {
        ZIKSession.appContext = context.getApplicationContext();
    }

    public ZIKSession() {

    }

    public void getRootAsync(@NonNull final String url, @NonNull final ZIKCallback<ZIKRoot> rootCallback) {
        new ZIKRootAsyncTask(this,url,rootCallback).execute();
    }

    public void getRootSync(@NonNull final String url, @NonNull final ZIKCallback<ZIKRoot> rootCallback) {
        try {
            Request request = this.requestBuilderWithURL(url).get().build();
            Response response = ZIKSession.httpClient.newCall(request).execute();
            ZIKRoot root = ZIKRoot.fromString(response.body().string());
            rootCallback.onSuccess(root);
        } catch( Exception e ) {
            rootCallback.onFailure(e);
        }
    }

    public void getServersAsync(@NonNull final ZIKRoot root, @NonNull final ZIKCallback<List<ZIKServer>> serversCallback) {
        new ZIKServersAsyncTask(this,root,serversCallback).execute();
    }

    public void getServersSync(@NonNull final ZIKRoot root, @NonNull final ZIKCallback<List<ZIKServer>> serversCallback) {
        List<ZIKServer> servers = null;
        final List<ZIKLink> serverLinks = root.getAllServerLinks();
        if( !serverLinks.isEmpty() ) {
            ArrayList<ZIKServer> loadedServers = new ArrayList<>();
            for( ZIKLink serverLink : serverLinks ) {
                try {
                    Request request = this.requestBuilderWithURL(serverLink.getHref()).get().build();
                    Response response = ZIKSession.httpClient.newCall(request).execute();
                    ZIKServer server = ZIKServer.fromString(response.body().string());
                    loadedServers.add(server);
                } catch( Exception exception ) {
                    serversCallback.onFailure(exception);
                    return;
                }
            }
            servers = loadedServers;
        }
        if( servers == null ) {
            servers = Collections.emptyList();
        }
        serversCallback.onSuccess(servers);
    }

    public void getDevicesAsync(@NonNull final ZIKServer server, @NonNull final ZIKCallback<List<ZIKDevice>> devicesCallback) {
        new ZIKDevicesAsyncTask(this,server,devicesCallback).execute();
    }

    public void getDevicesSync(@NonNull final ZIKServer server, @NonNull final ZIKCallback<List<ZIKDevice>> devicesCallback) {
        List<ZIKDevice> devices = null;
        List<ZIKDevice> serverDevices = server.getDevices();
        if( ! serverDevices.isEmpty() ) {
            ArrayList<ZIKDevice> loadedDevices = new ArrayList<>();
            for( ZIKDevice serverDevice : serverDevices ) {
                for( ZIKLink deviceLink : serverDevice.getLinks() ) {
                    if( deviceLink.isSelf() ) {
                        try {
                            Request request = this.requestBuilderWithURL(deviceLink.getHref()).get().build();
                            Response response = ZIKSession.httpClient.newCall(request).execute();
                            ZIKDevice device = ZIKDevice.fromString(response.body().string());
                            loadedDevices.add(device);
                        } catch( Exception exception ) {
                            devicesCallback.onFailure(exception);
                            return;
                        }
                    }
                }
            }
            devices = loadedDevices;
        }
        if( devices == null ) {
            devices = Collections.emptyList();
        }
        devicesCallback.onSuccess(devices);
    }

    public void setGlobalHeaders(@NonNull final HashMap<String,Object> headers) {
        this.headers = headers;
    }

    public void setHeader(@NonNull final String key, @NonNull final String value) {
        this.headers.put(key,value);
    }

    public void unsetHeader(@NonNull final String key) {
        this.headers.remove(key);
    }

    @NonNull
    protected Request.Builder requestBuilderWithURL(@NonNull final String url) throws IllegalArgumentException {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        this.addHeadersToRequest(requestBuilder);
        return requestBuilder;
    }

    private void addHeadersToRequest(@NonNull final Request.Builder requestBuilder) {
        for(Map.Entry<String,Object> headerEntry : this.headers.entrySet()) {
            String headerKey = headerEntry.getKey();
            String headerValue = headerEntry.getValue().toString();
            requestBuilder.addHeader(headerKey,headerValue);
        }
    }
}
