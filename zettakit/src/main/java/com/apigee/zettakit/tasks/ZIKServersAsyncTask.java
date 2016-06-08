package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKRoot;
import com.apigee.zettakit.ZIKServer;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.callbacks.ZIKServersCallback;

import java.util.Collections;
import java.util.List;

public class ZIKServersAsyncTask extends AsyncTask<Void,Void,Void> {
    @NonNull private final ZIKServersCallback serversCallback;
    @NonNull private final ZIKSession session;
    @NonNull private final ZIKRoot root;

    @Nullable private List<ZIKServer> servers;
    @Nullable private Exception exception;

    public ZIKServersAsyncTask(@NonNull final ZIKSession session, @NonNull final ZIKRoot root, @NonNull final ZIKServersCallback serversCallback) {
        this.session = session;
        this.root = root;
        this.serversCallback = serversCallback;
    }

    @Override @Nullable
    protected Void doInBackground(final Void... v) {
        session.getServersSync(this.root, new ZIKServersCallback() {
            @Override
            public void onSuccess(@NonNull List<ZIKServer> servers) {
                ZIKServersAsyncTask.this.servers = servers;
            }
            @Override
            public void onFailure(@NonNull Exception exception) {
                ZIKServersAsyncTask.this.exception = exception;
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if( exception != null ) {
            serversCallback.onFailure(exception);
        } else {
            List<ZIKServer> loadedServers = servers;
            if( loadedServers == null ) {
                loadedServers = Collections.emptyList();
            }
            serversCallback.onSuccess(loadedServers);
        }
    }
}
