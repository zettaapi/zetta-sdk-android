package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKRoot;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.callbacks.ZIKRootCallback;
import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class ZIKRootAsyncTask extends AsyncTask<Void,Void,Void> {
    @NonNull private final ZIKRootCallback rootCallback;
    @NonNull public final ZIKSession session;
    @NonNull public final String rootUrl;

    @Nullable public ZIKRoot root;
    @Nullable public IOException exception;

    public ZIKRootAsyncTask(@NonNull final ZIKSession session, @NonNull final String rootUrl, @NonNull final ZIKRootCallback rootCallback) {
        this.session = session;
        this.rootUrl = rootUrl;
        this.rootCallback = rootCallback;
    }

    @Override @NonNull
    protected Void doInBackground(final Void... v) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(rootUrl);
        session.addHeadersToRequest(requestBuilder);
        Request request = requestBuilder.get().build();
        try {
            Response response = ZIKSession.httpClient.newCall(request).execute();
            if( response.isSuccessful() ) {
                root = ZIKJsonUtils.createObjectFromJson(ZIKRoot.class,response.body().string());
            }
        } catch( IOException e ) {
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(@NonNull final Void aVoid) {
        if( root != null ) {
            rootCallback.onSuccess(root);
        } else {
            rootCallback.onError("");
        }
    }
}