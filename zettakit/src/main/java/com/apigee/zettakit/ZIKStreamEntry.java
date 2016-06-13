package com.apigee.zettakit;

import android.support.annotation.NonNull;

import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;

public class ZIKStreamEntry {
    @NonNull private final String topic;
    @NonNull private final Object data;
    private final long timeStamp;

    @NonNull public String getTopic() { return topic; }
    @NonNull public Object getData() { return data; }
    public long getTimeStamp() { return timeStamp; }

    @Override
    public String toString() { return ZIKJsonUtils.objectToJsonString(ZIKStreamEntry.class,this); }

    @NonNull
    public static ZIKStreamEntry fromString(@NonNull final String string) throws IOException {
        return ZIKJsonUtils.createObjectFromJson(ZIKStreamEntry.class,string);
    }

    public ZIKStreamEntry(@NonNull final String topic, final long timestamp, @NonNull final Object data) {
        this.topic = topic;
        this.timeStamp = timestamp;
        this.data = data;
    }

    @NonNull public String getTitle() {
        return topic.substring(topic.lastIndexOf("/" + 1), topic.length());
    }
}
