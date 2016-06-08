package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.listeners.ZIKStreamListener;
import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;

public class ZIKStream {
    private enum ZIKStreamState { OPEN, OPENING, CLOSED }

    private boolean pingWhileOpen = true;

    @NonNull  private final ZIKLink link;
    @Nullable private ZIKStreamListener streamListener;

    @NonNull  private ZIKStreamState streamState;
    @Nullable private Timer pingTimer;
    @Nullable private WebSocket webSocket;

    public ZIKStream(@NonNull final ZIKLink link) {
        this.link = link;
        this.streamState = ZIKStreamState.CLOSED;
    }

    public boolean isOpen() {
        return this.streamState == ZIKStreamState.OPEN;
    }

    public void setPingWhileOpen(final boolean pingWhileOpen) {
        this.pingWhileOpen = pingWhileOpen;
    }

    public void setStreamListener(@Nullable final ZIKStreamListener streamListener) {
        this.streamListener = streamListener;
    }

    private void cancelPingTimer() {
        if( pingTimer != null ) {
            pingTimer.cancel();
            pingTimer = null;
        }
    }

    private void schedulePing() {
        if( pingWhileOpen ) {
            this.cancelPingTimer();

            pingTimer = new Timer();
            pingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if( webSocket != null ) {
                        try {
                            webSocket.sendPing(new Buffer());
                        } catch ( Exception pingException ) {
                            ZIKStream.this.close();
                        }
                    }
                }
            }, 0, 2000);
        }
    }

    public void close() {
        if( webSocket != null ) {
            try {
                this.cancelPingTimer();
                webSocket.close(1000,"");
            } catch ( Exception closeException ) { }
        }
    }

    public void resume() {
        if( webSocket == null && this.streamState == ZIKStreamState.CLOSED ) {
            ZIKStream.this.streamState = ZIKStreamState.OPENING;

            final Request request = new Request.Builder().url(link.getHref()).build();
            WebSocketCall webSocketCall = WebSocketCall.create(ZIKSession.httpClient, request);
            webSocketCall.enqueue(new WebSocketListener() {

                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    ZIKStream.this.streamState = ZIKStreamState.OPEN;
                    ZIKStream.this.webSocket = webSocket;
                    ZIKStream.this.schedulePing();
                    if( streamListener != null ) {
                        streamListener.onOpen();
                    }
                }

                @Override
                public void onFailure(IOException e, Response response) {
                    ZIKStream.this.cancelPingTimer();
                    ZIKStream.this.webSocket = null;
                    if( streamListener != null ) {
                        streamListener.onError(e,response);
                    }
                }

                @Override
                public void onMessage(ResponseBody message) throws IOException {
                    String messageString = message.string();
                    message.close();

                    if( streamListener != null ) {
                        String linkTitle = ZIKStream.this.link.getTitle();
                        if( linkTitle != null && linkTitle.equalsIgnoreCase("logs") ) {
                            // TODO: Implement ZIKLogStreamEntry class.
                        } else if( ZIKStream.this.link.hasRel("http://rels.zettajs.io/query") ) {
                            ZIKDevice device = ZIKJsonUtils.createObjectFromJson(ZIKDevice.class,messageString);
                            streamListener.onUpdate(device);
                        } else {
                            ZIKStreamEntry streamEntry = ZIKJsonUtils.createObjectFromJson(ZIKStreamEntry.class,messageString);
                            streamListener.onUpdate(streamEntry);
                        }
                    }
                }

                @Override
                public void onPong(Buffer payload) {
                    if( streamListener != null ) {
                        streamListener.onPong();
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    ZIKStream.this.webSocket = null;
                    ZIKStream.this.streamState = ZIKStreamState.CLOSED;
                    ZIKStream.this.cancelPingTimer();

                    if( streamListener != null ) {
                        streamListener.onClose();
                    }
                }
            });
        }
    }
}
