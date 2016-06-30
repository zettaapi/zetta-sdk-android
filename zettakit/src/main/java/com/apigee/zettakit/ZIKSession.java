package com.apigee.zettakit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.interfaces.ZIKCallback;
import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ZIKSession {

    @Nullable private static Context appContext;

    @NonNull protected static final OkHttpClient httpClient = new OkHttpClient();
    @NonNull private static final ZIKSession sharedSession = new ZIKSession();
    @NonNull public static ZIKSession getSharedSession() {
        return ZIKSession.sharedSession;
    }
    @NonNull private HashMap<String,Object> headers = new HashMap<>();

    static {
        httpClient.dispatcher().setMaxRequests(Integer.MAX_VALUE);
        httpClient.dispatcher().setMaxRequestsPerHost(Integer.MAX_VALUE);
    }

    public static void init(@NonNull final Context context) {
        ZIKSession.appContext = context.getApplicationContext();
    }

    public ZIKSession() {

    }

    public ZIKRoot getRootSync(@NonNull final String url) throws ZIKException {
        return this.getRootObservable(url).toBlocking().value();
    }

    public void getRootAsync(@NonNull final String url, @NonNull final ZIKCallback<ZIKRoot> rootCallback) {
        this.getRootObservable(url).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ZIKRoot>() {
            @Override
            public void onCompleted() { /* unused */ }
            @Override
            public void onError(Throwable e) {
                rootCallback.onFailure(new ZIKException(e));
            }
            @Override
            public void onNext(ZIKRoot root) {
                rootCallback.onSuccess(root);
            }
        });
    }

    public Single<ZIKRoot> getRootObservable(@NonNull final String url) {
        return Single.fromCallable(new Callable<ZIKRoot>() {
            @Override
            public ZIKRoot call() {
                try {
                    return ZIKSession.this.getRootWithURL(url);
                } catch (Exception exception) {
                    throw new ZIKException(exception);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public List<ZIKServer> getServersSync(@NonNull final ZIKRoot root) throws ZIKException {
        return this.getServersObservable(root).toList().toBlocking().single();
    }

    public List<ZIKServer> getServersSync(@NonNull final List<ZIKLink> serverLinks) throws ZIKException {
        return this.getServersObservable(serverLinks).toList().toBlocking().single();
    }

    public void getServersAsync(@NonNull final ZIKRoot root, @NonNull final ZIKCallback<List<ZIKServer>> serversCallback) {
        this.getServersAsync(root.getAllServerLinks(),serversCallback);
    }

    public void getServersAsync(@NonNull final List<ZIKLink> serverLinks, @NonNull final ZIKCallback<List<ZIKServer>> serversCallback) {
        final ArrayList<ZIKServer> servers = new ArrayList<>();
        this.getServersObservable(serverLinks).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ZIKServer>() {
            @Override
            public void onCompleted() {
                serversCallback.onSuccess(servers);
            }
            @Override
            public void onError(Throwable e) {
                serversCallback.onFailure(new ZIKException(e));
            }
            @Override
            public void onNext(ZIKServer server) {
                servers.add(server);
            }
        });
    }

    @NonNull
    public Observable<ZIKServer> getServersObservable(@NonNull final ZIKRoot root) {
        return this.getServersObservable(root.getAllServerLinks());
    }

    @NonNull
    public Observable<ZIKServer> getServersObservable(@NonNull final List<ZIKLink> serverLinks) {
        return Observable.from(serverLinks).map(new Func1<ZIKLink, ZIKServer>() {
            @Override
            public ZIKServer call(ZIKLink serverLink) {
                try {
                    return ZIKSession.this.getServerWithLink(serverLink);
                } catch (Exception exception) {
                    throw new ZIKException(exception);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public List<ZIKDevice> getDevicesSync(@NonNull final ZIKServer server) throws ZIKException {
        return this.getDevicesObservable(server).toList().toBlocking().single();
    }

    public List<ZIKDevice> getDevicesSync(@NonNull final List<ZIKLink> deviceLinks) throws ZIKException {
        return this.getDevicesObservable(deviceLinks).toList().toBlocking().single();
    }

    public void getDevicesAsync(@NonNull final ZIKServer server, @NonNull final ZIKCallback<List<ZIKDevice>> devicesCallback) {
        this.getDevicesAsync(server.getAllDeviceSelfLinks(),devicesCallback);
    }

    public void getDevicesAsync(@NonNull final List<ZIKLink> deviceLinks, @NonNull final ZIKCallback<List<ZIKDevice>> devicesCallback) {
        final ArrayList<ZIKDevice> devices = new ArrayList<>();
        this.getDevicesObservable(deviceLinks).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ZIKDevice>() {
            @Override
            public void onCompleted() {
                devicesCallback.onSuccess(devices);
            }
            @Override
            public void onError(Throwable e) {
                devicesCallback.onFailure(new ZIKException(e));
            }
            @Override
            public void onNext(ZIKDevice device) {
                devices.add(device);
            }
        });
    }

    @NonNull
    public Observable<ZIKDevice> getDevicesObservable(@NonNull final ZIKServer server) {
        return this.getDevicesObservable(server.getAllDeviceSelfLinks());
    }

    @NonNull
    public Observable<ZIKDevice> getDevicesObservable(@NonNull final List<ZIKLink> deviceLinks) {
        return Observable.from(deviceLinks).map(new Func1<ZIKLink, ZIKDevice>() {
            @Override
            public ZIKDevice call(ZIKLink deviceLink) {
                try {
                    return ZIKSession.this.getDeviceWithLink(deviceLink);
                } catch (Exception exception) {
                    throw new ZIKException(exception);
                }
            }
        }).subscribeOn(Schedulers.io());
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
    protected static Response performRequest(@NonNull final Request request) throws ZIKException {
        try {
            return ZIKSession.httpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new ZIKException(e);
        }
    }

    @NonNull
    protected Request.Builder requestBuilderWithURL(@NonNull final String url) throws ZIKException {
        try {
            Request.Builder requestBuilder = new Request.Builder().url(url);
            this.addHeadersToRequest(requestBuilder);
            return requestBuilder;
        } catch (Exception e) {
            throw new ZIKException(e);
        }
    }

    @NonNull
    protected ZIKRoot getRootWithURL(@NonNull final String url) throws Exception {
        return this.getObjectWithUrl(ZIKRoot.class,url);
    }

    @NonNull
    protected ZIKServer getServerWithLink(@NonNull final ZIKLink serverLink) throws Exception {
        return this.getObjectWithUrl(ZIKServer.class,serverLink.getHref());
    }

    @NonNull
    protected ZIKDevice getDeviceWithLink(@NonNull final ZIKLink deviceLink) throws Exception {
        return this.getObjectWithUrl(ZIKDevice.class,deviceLink.getHref());
    }

    protected <T> T getObjectWithUrl(@NonNull final Class<T> objectClass, @NonNull final String url) throws Exception {
        Request request = this.requestBuilderWithURL(url).get().build();
        Response response = performRequest(request);
        return ZIKJsonUtils.createObjectFromJson(objectClass,response.body().string());
    }

    private void addHeadersToRequest(@NonNull final Request.Builder requestBuilder) {
        for(Map.Entry<String,Object> headerEntry : this.headers.entrySet()) {
            String headerKey = headerEntry.getKey();
            String headerValue = headerEntry.getValue().toString();
            requestBuilder.addHeader(headerKey,headerValue);
        }
    }
}
