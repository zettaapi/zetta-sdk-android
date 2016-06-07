package com.apigee.zettakit.jsonHelpers;

import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKTransition;

import java.util.List;
import java.util.Map;

public final class ZIKDeviceJson {
    Map<String,Object> properties;
    List<ZIKLink> links;
    List<ZIKTransition> actions;
}
