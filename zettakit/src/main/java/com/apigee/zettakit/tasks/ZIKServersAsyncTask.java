package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKRoot;
import com.apigee.zettakit.ZIKServer;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.callbacks.ZIKServersCallback;
import com.apigee.zettakit.utils.ZIKJsonUtils;
import com.apigee.zettakit.utils.ZIKUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ZIKServersAsyncTask extends AsyncTask<Void,Void,Void> {
    private static final String SERVER = "server";

    @NonNull private final ZIKServersCallback serversCallback;
    @NonNull private final ZIKSession session;
    @NonNull private final ZIKRoot root;
    @NonNull private final ArrayList<ZIKServer> servers = new ArrayList<ZIKServer>();

    public ZIKServersAsyncTask(@NonNull final ZIKSession session, @NonNull final ZIKRoot root, @NonNull final ZIKServersCallback serversCallback) {
        this.session = session;
        this.root = root;
        this.serversCallback = serversCallback;
    }

    @Override @NonNull
    protected Void doInBackground(final Void... v) {
        final ArrayList<ZIKLink> serverLinks = new ArrayList<ZIKLink>();
        List<ZIKLink> rootLinks = root.getLinks();
        if( !rootLinks.isEmpty() ) {
            String serverRel = ZIKUtils.generateRelForString(SERVER);
            for( ZIKLink rootLink : rootLinks ) {
                if( rootLink.hasRel(serverRel)) {
                    serverLinks.add(rootLink);
                }
            }
        }
        if( !serverLinks.isEmpty() ) {
            for( ZIKLink serverLink : serverLinks ) {
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url(serverLink.getHref());
                session.addHeadersToRequest(requestBuilder);
                Request request = requestBuilder.get().build();
                try {
                    Response response = ZIKSession.httpClient.newCall(request).execute();
                    if( response.isSuccessful() ) {
                        ZIKServer server = ZIKJsonUtils.createObjectFromJson(ZIKServer.class,response.body().string());
                        this.servers.add(server);
                    }
                } catch( IOException ignored ) { }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(@NonNull final Void aVoid) {
        serversCallback.onFinished(servers);
    }
}
