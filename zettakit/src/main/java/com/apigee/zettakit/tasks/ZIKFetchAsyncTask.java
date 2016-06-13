package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.interfaces.ZIKCallback;
import com.apigee.zettakit.interfaces.ZIKFetchable;

import java.io.IOException;

public final class ZIKFetchAsyncTask<T> extends AsyncTask<Void,Void,Void> {
    @NonNull private final ZIKSession session;

    @NonNull private final ZIKCallback<T> callback;
    @NonNull private final ZIKFetchable<T> fetcher;

    @Nullable private T fetchedObject;
    @Nullable private Exception exception;

    public ZIKFetchAsyncTask(@NonNull final ZIKSession session, @NonNull final ZIKFetchable<T> fetcher, @NonNull final ZIKCallback<T> callback) {
        this.session = session;
        this.fetcher = fetcher;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(final Void... v) {
        fetcher.fetchSync(session, new ZIKCallback<T>() {
            @Override
            public void onSuccess(@NonNull final T loadedObject) {
                ZIKFetchAsyncTask.this.fetchedObject = loadedObject;
            }
            @Override
            public void onFailure(@NonNull final Exception exception) {
                ZIKFetchAsyncTask.this.exception = exception;
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(final Void aVoid) {
        if( exception != null ) {
            callback.onFailure(exception);
        } else if( fetchedObject != null ) {
            callback.onSuccess(this.fetchedObject);
        } else {
            // This realistically shouldn't happen.
            callback.onFailure(new IOException("Fetching object failed without an exception."));
        }
    }
}

