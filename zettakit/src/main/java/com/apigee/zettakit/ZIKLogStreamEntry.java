package com.apigee.zettakit;

import android.support.annotation.NonNull;

import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.util.List;
import java.util.Map;

public class ZIKLogStreamEntry {
    @NonNull private final String topic;
    @NonNull private final String transition;
    @NonNull private final String deviceState;
    @NonNull private final Map<String,Object> properties;
    @NonNull private final List inputs;
    @NonNull private final List<ZIKTransition> transitions;
    private final long timeStamp;

    @NonNull public String getTopic() { return topic; }
    @NonNull public String getTransition() { return transition; }
    @NonNull public String getDeviceState() { return deviceState; }
    @NonNull public Map<String, Object> getProperties() { return properties; }
    @NonNull public List getInputs() { return inputs; }
    @NonNull public List<ZIKTransition> getTransitions() { return transitions; }
    public long getTimeStamp() { return timeStamp; }

    @Override
    public String toString() { return ZIKJsonUtils.objectToJsonString(ZIKLogStreamEntry.class,this); }

    @NonNull
    public static ZIKLogStreamEntry fromString(@NonNull final String string) throws ZIKException {
        return ZIKJsonUtils.createObjectFromJson(ZIKLogStreamEntry.class,string);
    }

    public ZIKLogStreamEntry(@NonNull final String topic,
                             @NonNull final String transition,
                             @NonNull final Map<String,Object> properties,
                             @NonNull final List inputs,
                             @NonNull final List<ZIKTransition> transitions,
                             final long timestamp) {
        this.topic = topic;
        this.transition = transition;
        this.properties = properties;
        this.inputs = inputs;
        this.transitions = transitions;
        this.timeStamp = timestamp;

        Object deviceState = properties.get("state");
        if( deviceState == null ) {
            deviceState = "";
        }
        this.deviceState = deviceState.toString();
    }
}
