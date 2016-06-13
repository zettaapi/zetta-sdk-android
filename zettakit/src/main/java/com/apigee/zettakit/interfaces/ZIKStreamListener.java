package com.apigee.zettakit.interfaces;

import okhttp3.Response;

public interface ZIKStreamListener {
    void onOpen();
    void onError(Exception exception, Response response);
    void onPong();
    void onUpdate(Object object);
    void onClose();
}
