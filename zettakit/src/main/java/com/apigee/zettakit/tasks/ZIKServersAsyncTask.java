package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKRoot;
import com.apigee.zettakit.ZIKServer;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.callbacks.ZIKServersCallback;
import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ZIKServersAsyncTask extends AsyncTask<Void,Void,List<ZIKServer>> {
    @NonNull private final ZIKServersCallback serversCallback;
    @NonNull private final ZIKSession session;
    @NonNull private final ZIKRoot root;

    public ZIKServersAsyncTask(@NonNull final ZIKSession session, @NonNull final ZIKRoot root, @NonNull final ZIKServersCallback serversCallback) {
        this.session = session;
        this.root = root;
        this.serversCallback = serversCallback;
    }

    @Override @Nullable
    protected List<ZIKServer> doInBackground(final Void... v) {
        ArrayList<ZIKServer> loadedServers = null;
        final List<ZIKLink> serverLinks = root.getAllServerLinks();
        if( !serverLinks.isEmpty() ) {
            loadedServers = new ArrayList<>();
            for( ZIKLink serverLink : serverLinks ) {
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url(serverLink.getHref());
                session.addHeadersToRequest(requestBuilder);
                Request request = requestBuilder.get().build();
                try {
                    Response response = ZIKSession.httpClient.newCall(request).execute();
                    if( response.isSuccessful() ) {
                        loadedServers.add(ZIKJsonUtils.createObjectFromJson(ZIKServer.class,response.body().string()));
                    }
                } catch( IOException ignored ) { }
            }
        }
        return loadedServers;
    }

    @Override
    protected void onPostExecute(@Nullable final List<ZIKServer> servers) {
        List<ZIKServer> loadedServers = servers;
        if( loadedServers == null ) {
            loadedServers = Collections.emptyList();
        }
        serversCallback.onFinished(loadedServers);
    }
}
