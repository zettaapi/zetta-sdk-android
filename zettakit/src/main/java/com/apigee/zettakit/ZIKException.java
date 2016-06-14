package com.apigee.zettakit;

public class ZIKException extends RuntimeException {

    public ZIKException(String detailMessage) {
        super(detailMessage);
    }

    public ZIKException(Throwable e) {
        super(e);
    }
}
