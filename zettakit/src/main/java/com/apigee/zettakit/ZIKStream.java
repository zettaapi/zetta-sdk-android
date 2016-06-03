package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;

public class ZIKStream {
    private final ZIKLink link;
    private final WebSocketCall webSocketCall;

    public ZIKStream(@NonNull final ZIKLink link) {
        this.link = link;
        final Request request = new Request.Builder().url(link.getHref()).build();
        this.webSocketCall = WebSocketCall.create(ZIKSession.httpClient, request);
    }

    public void open() {
        webSocketCall.enqueue(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d("xxx", "open " + response);
            }

            @Override
            public void onFailure(IOException e, Response response) {
                Log.d("xxx", "failure " + response);
                Log.e("xxx", "failure ", e);
            }

            @Override
            public void onMessage(ResponseBody message) throws IOException {
                Log.d("xxx", "message " + message.string());
                message.close();
            }

            @Override
            public void onPong(Buffer payload) {
                Log.d("xxx", "pong" + payload);
            }

            @Override
            public void onClose(int code, String reason) {
                Log.d("xxx", "close " + code + " " + reason);
            }
        });
    }
}
