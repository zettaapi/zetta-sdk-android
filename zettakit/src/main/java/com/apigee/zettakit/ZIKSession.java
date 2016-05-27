package com.apigee.zettakit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.callbacks.ZIKDevicesCallback;
import com.apigee.zettakit.callbacks.ZIKRootCallback;
import com.apigee.zettakit.callbacks.ZIKServersCallback;
import com.apigee.zettakit.tasks.ZIKDevicesAsyncTask;
import com.apigee.zettakit.tasks.ZIKServersAsyncTask;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZIKSession {

    @Nullable private static Context appContext;
    @NonNull private static ZIKSession sharedSession = new ZIKSession();
    @NonNull public static ZIKSession getSharedSession() {
        return ZIKSession.sharedSession;
    }

    @Nullable public HttpUrl apiEndpoint;
    @NonNull private HashMap<String,Object> headers = new HashMap<>();
    @NonNull public OkHttpClient httpClient = new OkHttpClient();
    @NonNull public ObjectMapper jsonMapper = new ObjectMapper();

    public static void init(@NonNull final Context context) {
        ZIKSession.appContext = context.getApplicationContext();
    }

    public ZIKSession() {
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void addHeadersToRequest(@NonNull final Request.Builder requestBuilder) {
        for(Map.Entry<String,Object> headerEntry : this.headers.entrySet()) {
            String headerKey = headerEntry.getKey();
            String headerValue = headerEntry.getValue().toString();
            requestBuilder.addHeader(headerKey,headerValue);
        }
    }

    public void getRoot(@NonNull final String url, @NonNull final ZIKRootCallback rootCallback) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        this.addHeadersToRequest(requestBuilder);
        requestBuilder.get().build();
        httpClient.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                rootCallback.onError(e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if( response.isSuccessful() ) {
                    JsonNode jsonNode = jsonMapper.readTree(response.body().string());
                    if( jsonNode != null ) {
                        JsonParser jsonParser = jsonNode.traverse();
                        ZIKRoot root = jsonMapper.readValue(jsonParser,ZIKRoot.class);
                        rootCallback.onSuccess(root);
                    }
                }
            }
        });
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
