package com.apigee.zettakit.jsonHelpers;

import com.apigee.zettakit.ZIKDevice;
import com.apigee.zettakit.ZIKLink;
import com.apigee.zettakit.ZIKTransition;

import java.util.List;
import java.util.Map;

public final class ZIKServerJson {
    Map<String,Object> properties;
    List<ZIKDevice> entities;
    List<ZIKLink> links;
    List<ZIKTransition> actions;
}
