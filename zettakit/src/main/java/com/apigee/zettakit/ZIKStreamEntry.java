package com.apigee.zettakit;

import android.support.annotation.NonNull;

import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.util.UUID;

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
    public static ZIKStreamEntry fromString(@NonNull final String string) throws ZIKException {
        return ZIKJsonUtils.createObjectFromJson(ZIKStreamEntry.class,string);
    }

    public ZIKStreamEntry(@NonNull final String topic, final long timestamp, @NonNull final Object data) {
        this.topic = topic;
        this.timeStamp = timestamp;
        this.data = data;
    }

    @NonNull
    public String getName() {
        return topic.substring(0, topic.indexOf("/"));
    }

    @NonNull
    public UUID getUUID() {
        int positionFirstBreak = topic.indexOf("/");
        int positionSecondBreak = topic.indexOf("/", positionFirstBreak + 1);
        return UUID.fromString(topic.substring(positionFirstBreak + 1, positionSecondBreak - 1));
    }

    @NonNull
    public String getTitle() {
        int positionThirdBreak = topic.lastIndexOf("/");
        return topic.substring(positionThirdBreak + 1, topic.length());
    }
}
