package com.apigee.zettakit.jsonHelpers;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKLogStreamEntry;
import com.apigee.zettakit.ZIKTransition;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ZIKLogStreamEntryJsonAdapter {
    @FromJson
    public ZIKLogStreamEntry logStreamEntryFromJson(@NonNull final ZIKLogStreamEntryJson logStreamEntryJson) {
        String topic = logStreamEntryJson.topic;
        if( topic == null ) {
            topic = "";
        }
        String transition = logStreamEntryJson.transition;
        if( transition == null ) {
            transition = "";
        }
        Map<String,Object> properties = logStreamEntryJson.properties;
        if( properties == null ) {
            properties = Collections.emptyMap();
        }
        List inputs = logStreamEntryJson.inputs;
        if( inputs == null ) {
            inputs = Collections.emptyList();
        }
        List<ZIKTransition> actions = logStreamEntryJson.actions;
        if( actions == null ) {
            actions = Collections.emptyList();
        }
        return new ZIKLogStreamEntry(topic,transition,properties,inputs,actions,logStreamEntryJson.timestamp);
    }

    @ToJson
    public ZIKLogStreamEntryJson logStreamEntryToJson(@NonNull final ZIKLogStreamEntry logStreamEntry) {
        ZIKLogStreamEntryJson json = new ZIKLogStreamEntryJson();
        json.topic = logStreamEntry.getTopic();
        json.transition = logStreamEntry.getTransition();
        json.properties = logStreamEntry.getProperties();
        json.inputs = logStreamEntry.getInputs();
        json.actions = logStreamEntry.getTransitions();
        json.timestamp = logStreamEntry.getTimeStamp();
        return json;
    }
}
