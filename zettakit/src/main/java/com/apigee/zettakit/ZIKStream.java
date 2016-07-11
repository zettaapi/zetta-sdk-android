package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.interfaces.ZIKStreamListener;
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

public class ZIKStream implements Parcelable {
    private enum ZIKStreamState { OPEN, OPENING, CLOSED }

    private boolean pingWhileOpen = true;

    @NonNull  private final ZIKLink link;
    @Nullable private ZIKStreamListener streamListener;

    @NonNull  private ZIKStreamState streamState;
    @Nullable private Timer pingTimer;
    @Nullable private WebSocket webSocket;

    @Override
    public String toString() { return ZIKJsonUtils.objectToJsonString(ZIKLink.class,this.link); }

    public ZIKStream(@NonNull final ZIKLink link) {
        this.link = link;
        this.streamState = ZIKStreamState.CLOSED;
    }

    public boolean isOpen() {
        return this.streamState == ZIKStreamState.OPEN;
    }

    public String getTitle() {
        return link.getTitle();
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
                this.streamState = ZIKStreamState.CLOSED;
                this.cancelPingTimer();
                webSocket.close(1000,"");
            } catch ( Exception ignored ) { }
        }
    }

    public void resume() {
        if( webSocket == null && this.streamState == ZIKStreamState.CLOSED ) {
            ZIKStream.this.streamState = ZIKStreamState.OPENING;

            Request request;
            try {
                request = ZIKSession.getSharedSession().requestBuilderWithURL(link.getHref()).build();
            } catch ( IllegalArgumentException exception ) {
                ZIKStream.this.streamState = ZIKStreamState.CLOSED;
                if( streamListener != null ) {
                    streamListener.onError(new ZIKException(exception),null);
                }
                return;
            }

            final WebSocketCall webSocketCall = WebSocketCall.create(ZIKSession.httpClient, request);
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
                    ZIKStream.this.streamState = ZIKStreamState.CLOSED;
                    ZIKStream.this.cancelPingTimer();
                    ZIKStream.this.webSocket = null;
                    if( streamListener != null ) {
                        streamListener.onError(new ZIKException(e),response);
                    }
                }

                @Override
                public void onMessage(ResponseBody message) throws IOException {
                    try {
                        if( ZIKStream.this.isOpen() && streamListener != null ) {
                            final String messageString = message.string();
                            final ZIKLink link = ZIKStream.this.link;
                            final String linkTitle = link.getTitle();
                            if (linkTitle != null && linkTitle.equalsIgnoreCase("logs")) {
                                ZIKLogStreamEntry logStreamEntry = ZIKLogStreamEntry.fromString(messageString);
                                streamListener.onUpdate(logStreamEntry);
                            } else if (link.hasRel("http://rels.zettajs.io/query")) {
                                ZIKDevice device = ZIKDevice.fromString(messageString);
                                streamListener.onUpdate(device);
                            } else {
                                ZIKStreamEntry streamEntry = ZIKStreamEntry.fromString(messageString);
                                streamListener.onUpdate(streamEntry);
                            }
                        }
                    } catch (Exception exception) {
                        if(streamListener != null) {
                            streamListener.onError(new ZIKException(exception),null);
                        }
                    } finally {
                        message.close();
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
                    ZIKStream.this.streamState = ZIKStreamState.CLOSED;
                    ZIKStream.this.cancelPingTimer();
                    ZIKStream.this.webSocket = null;
                    if( streamListener != null ) {
                        streamListener.onClose();
                    }
                }
            });
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeByte(this.pingWhileOpen ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.link, flags);
    }

    protected ZIKStream(final Parcel in) {
        this.pingWhileOpen = in.readByte() != 0;
        this.link = in.readParcelable(ZIKLink.class.getClassLoader());
        this.streamState = ZIKStreamState.CLOSED;
    }

    public static final Parcelable.Creator<ZIKStream> CREATOR = new Parcelable.Creator<ZIKStream>() {
        @Override
        public ZIKStream createFromParcel(Parcel source) {
            return new ZIKStream(source);
        }
        @Override
        public ZIKStream[] newArray(int size) {
            return new ZIKStream[size];
        }
    };
}
