package com.apigee.zettakit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKRoot;
import com.apigee.zettakit.ZIKServer;
import com.apigee.zettakit.ZIKSession;
import com.apigee.zettakit.ZIKUtils;
import com.apigee.zettakit.callbacks.ZIKServersCallback;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ZIKServersAsyncTask extends AsyncTask<Void,Void,Void> {
    @NonNull private final ZIKServersCallback serversCallback;
    @NonNull private final ZIKSession session;
    @NonNull private final ZIKRoot root;
    @Nullable public List<ZIKServer> servers;

    public ZIKServersAsyncTask(@NonNull final ZIKSession session, @NonNull final ZIKRoot root, @NonNull final ZIKServersCallback serversCallback) {
        this.session = session;
        this.root = root;
        this.serversCallback = serversCallback;
    }

    @Override @NonNull
    protected Void doInBackground(final Void... v) {
        final ArrayList<ZIKLink> serverLinks = new ArrayList<>();
        List<ZIKLink> rootLinks = root.getLinks();
        if( rootLinks != null && !rootLinks.isEmpty() ) {
            String serverRel = ZIKUtils.generateRelForString("server");
            for( ZIKLink rootLink : rootLinks ) {
                if( rootLink.hasRel(serverRel)) {
                    serverLinks.add(rootLink);
                }
            }
        }
        ArrayList<ZIKServer> servers = new ArrayList<>();
        if( !serverLinks.isEmpty() ) {
            for( ZIKLink serverLink : serverLinks ) {
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url(serverLink.getHref());
                session.addHeadersToRequest(requestBuilder);
                Request request = requestBuilder.get().build();
                try {
                    Response response = ZIKSession.httpClient.newCall(request).execute();
                    if( response.isSuccessful() ) {
                        JsonNode jsonNode = ZIKSession.jsonMapper.readTree(response.body().string());
                        if( jsonNode != null ) {
                            JsonParser jsonParser = jsonNode.traverse();
                            ZIKServer server = ZIKSession.jsonMapper.readValue(jsonParser,ZIKServer.class);
                            servers.add(server);
                        }
                    }
                } catch( IOException ignored ) { }
            }
        }
        this.servers = servers;
        return null;
    }

    @Override
    protected void onPostExecute(@NonNull final Void aVoid) {
        serversCallback.onFinished(servers);
    }
}
