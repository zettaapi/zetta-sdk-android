package com.apigee.zettakit.interfaces;

import java.io.IOException;

import okhttp3.Response;

public interface ZIKStreamListener {
    void onOpen();
    void onError(IOException exception, Response response);
    void onPong();
    void onUpdate(Object object);
    void onClose();
}
