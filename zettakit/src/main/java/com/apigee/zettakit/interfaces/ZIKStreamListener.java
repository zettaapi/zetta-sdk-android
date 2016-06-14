package com.apigee.zettakit.interfaces;

import com.apigee.zettakit.ZIKException;

import okhttp3.Response;

public interface ZIKStreamListener {
    void onOpen();
    void onError(ZIKException exception, Response response);
    void onPong();
    void onUpdate(Object object);
    void onClose();
}
