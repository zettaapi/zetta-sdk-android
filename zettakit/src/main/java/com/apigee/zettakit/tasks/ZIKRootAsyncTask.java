package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKRoot;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.callbacks.ZIKRootCallback;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class ZIKRootAsyncTask extends AsyncTask<Void,Void,Void> {
    @NonNull private final ZIKRootCallback rootCallback;
    @NonNull private final ZIKSession session;
    @NonNull private final String rootUrl;

    @Nullable private ZIKRoot root;
    @Nullable private IOException exception;

    public ZIKRootAsyncTask(@NonNull final ZIKSession session, @NonNull final String rootUrl, @NonNull final ZIKRootCallback rootCallback) {
        this.session = session;
        this.rootUrl = rootUrl;
        this.rootCallback = rootCallback;
    }

    @Override @NonNull
    protected Void doInBackground(final Void... v) {
        session.getRootSync(this.rootUrl, new ZIKRootCallback() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                ZIKRootAsyncTask.this.root = root;
            }
            @Override
            public void onFailure(@NonNull IOException exception) {
                ZIKRootAsyncTask.this.exception = exception;
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(@NonNull final Void aVoid) {
        if( exception != null ) {
            rootCallback.onFailure(exception);
        } else {
            ZIKRoot root = this.root;
            if( root == null ) {
                root = new ZIKRoot(Collections.<ZIKLink>emptyList(),Collections.<Map<String,Object>>emptyList());
            }
            rootCallback.onSuccess(root);
        }
    }
}