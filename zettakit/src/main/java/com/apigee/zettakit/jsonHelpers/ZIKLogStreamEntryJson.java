package com.apigee.zettakit.jsonHelpers;

import com.apigee.zettakit.ZIKTransition;

import java.util.List;
import java.util.Map;

public final class ZIKLogStreamEntryJson {
    String topic;
    String transition;
    Map<String,Object> properties;
    List inputs;
    List<ZIKTransition> actions;
    long timestamp;
}
