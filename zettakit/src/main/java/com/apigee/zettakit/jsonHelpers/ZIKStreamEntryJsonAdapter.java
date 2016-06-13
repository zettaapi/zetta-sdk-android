package com.apigee.zettakit.jsonHelpers;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKStreamEntry;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public final class ZIKStreamEntryJsonAdapter {
    @FromJson
    public ZIKStreamEntry streamEntryFromJson(@NonNull final ZIKStreamEntryJson streamEntryJson) {
        String topic = streamEntryJson.topic;
        if( topic == null ) {
            topic = "";
        }
        return new ZIKStreamEntry(topic,streamEntryJson.timestamp,streamEntryJson.data);
    }
    @ToJson
    public ZIKStreamEntryJson streamEntryToJson(@NonNull final ZIKStreamEntry streamEntry) {
        ZIKStreamEntryJson json = new ZIKStreamEntryJson();
        json.topic = streamEntry.getTopic();
        json.timestamp = streamEntry.getTimeStamp();
        json.data = streamEntry.getData();
        return json;
    }
}
