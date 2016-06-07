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

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ZIKSession {

    @Nullable private static Context appContext;

    @NonNull public static OkHttpClient httpClient = new OkHttpClient();
    @NonNull private static ZIKSession sharedSession = new ZIKSession();

    @NonNull public static ZIKSession getSharedSession() {
        return ZIKSession.sharedSession;
    }

    @NonNull private HashMap<String,Object> headers = new HashMap<>();

    public static void init(@NonNull final Context context) {
        ZIKSession.appContext = context.getApplicationContext();
    }

    public ZIKSession() {

    }

    public void addHeadersToRequest(@NonNull final Request.Builder requestBuilder) {
        for(Map.Entry<String,Object> headerEntry : this.headers.entrySet()) {
            String headerKey = headerEntry.getKey();
            String headerValue = headerEntry.getValue().toString();
            requestBuilder.addHeader(headerKey,headerValue);
        }
    }

    public void getRoot(@NonNull final String url, @NonNull final ZIKRootCallback rootCallback) {
        new ZIKRootAsyncTask(this,url,rootCallback).execute();
    }

    public void getServers(@NonNull final ZIKRoot root, @NonNull final ZIKServersCallback serversCallback) {
        new ZIKServersAsyncTask(this,root,serversCallback).execute();
    }

    public void getDevices(@NonNull final ZIKServer server, @NonNull final ZIKDevicesCallback devicesCallback) {
        new ZIKDevicesAsyncTask(this,server,devicesCallback).execute();
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
}
