package com.apigee.zettakit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.callbacks.ZIKDevicesCallback;
import com.apigee.zettakit.callbacks.ZIKRootCallback;
import com.apigee.zettakit.callbacks.ZIKServersCallback;
import com.apigee.zettakit.tasks.ZIKDevicesAsyncTask;
import com.apigee.zettakit.tasks.ZIKRootAsyncTask;
import com.apigee.zettakit.tasks.ZIKServersAsyncTask;
import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;
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
    @NonNull private HashMap<String,Object> headers = new HashMap<String,Object>();

    public static void init(@NonNull final Context context) {
        ZIKSession.appContext = context.getApplicationContext();
    }

    public ZIKSession() {

    }

    public void getRootAsync(@NonNull final String url, @NonNull final ZIKRootCallback rootCallback) {
        new ZIKRootAsyncTask(this,url,rootCallback).execute();
    }

    public void getRootSync(@NonNull final String url, @NonNull final ZIKRootCallback rootCallback) {
        Request request = this.requestBuilderWithURL(url).get().build();
        try {
            Response response = ZIKSession.httpClient.newCall(request).execute();
            if( response.isSuccessful() ) {
                rootCallback.onSuccess(ZIKJsonUtils.createObjectFromJson(ZIKRoot.class,response.body().string()));
            }
        } catch( Exception e ) {
            rootCallback.onFailure(e);
        }
    }

    public void getServersAsync(@NonNull final ZIKRoot root, @NonNull final ZIKServersCallback serversCallback) {
        new ZIKServersAsyncTask(this,root,serversCallback).execute();
    }

    public void getServersSync(@NonNull final ZIKRoot root, @NonNull final ZIKServersCallback serversCallback) {
        List<ZIKServer> servers = null;
        final List<ZIKLink> serverLinks = root.getAllServerLinks();
        if( !serverLinks.isEmpty() ) {
            ArrayList<ZIKServer> loadedServers = new ArrayList<>();
            for( ZIKLink serverLink : serverLinks ) {
                Request request = this.requestBuilderWithURL(serverLink.getHref()).get().build();
                try {
                    Response response = ZIKSession.httpClient.newCall(request).execute();
                    if( response.isSuccessful() ) {
                        loadedServers.add(ZIKJsonUtils.createObjectFromJson(ZIKServer.class,response.body().string()));
                    }
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

    public void getDevicesAsync(@NonNull final ZIKServer server, @NonNull final ZIKDevicesCallback devicesCallback) {
        new ZIKDevicesAsyncTask(this,server,devicesCallback).execute();
    }

    public void getDevicesSync(@NonNull final ZIKServer server, @NonNull final ZIKDevicesCallback devicesCallback) {
        List<ZIKDevice> devices = null;
        List<ZIKDevice> serverDevices = server.getDevices();
        if( ! serverDevices.isEmpty() ) {
            ArrayList<ZIKDevice> loadedDevices = new ArrayList<>();
            for( ZIKDevice serverDevice : serverDevices ) {
                for( ZIKLink deviceLink : serverDevice.getLinks() ) {
                    if( deviceLink.isSelf() ) {
                        Request request = this.requestBuilderWithURL(deviceLink.getHref()).get().build();
                        try {
                            Response response = ZIKSession.httpClient.newCall(request).execute();
                            if( response.isSuccessful() ) {
                                loadedDevices.add(ZIKJsonUtils.createObjectFromJson(ZIKDevice.class,response.body().string()));
                            }
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
    protected Request.Builder requestBuilderWithURL(@NonNull final String url) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        this.addHeadersToRequest(requestBuilder);
        return requestBuilder;
    }

    protected void addHeadersToRequest(@NonNull final Request.Builder requestBuilder) {
        for(Map.Entry<String,Object> headerEntry : this.headers.entrySet()) {
            String headerKey = headerEntry.getKey();
            String headerValue = headerEntry.getValue().toString();
            requestBuilder.addHeader(headerKey,headerValue);
        }
    }
}
