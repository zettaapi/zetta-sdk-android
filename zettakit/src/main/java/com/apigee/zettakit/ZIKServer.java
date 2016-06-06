package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public class ZIKServer {

    @Nullable private final String name;
    @Nullable private final Map<String,JsonNode> properties;

    @Nullable private List<ZIKDevice> devices;
    @Nullable private List<ZIKLink> links;
    @Nullable private List<ZIKTransition> transitions;
    @Nullable private ZIKStyle style;

    public ZIKServer(@JsonProperty("properties") @NonNull final Map<String,JsonNode> properties) {
        this.properties = properties;
        final JsonNode nameNode = properties.get("name");
        if( nameNode != null ) {
            this.name = nameNode.asText(null);
        } else {
            this.name = null;
        }
        final JsonNode styleNode = properties.get("style");
        if( styleNode != null ) {
            this.style = ZIKSession.jsonMapper.convertValue(styleNode,ZIKStyle.class);
        } else {
            this.style = null;
        }
    }

    @Nullable @JsonIgnore
    public String getName() { return this.name; }

    @Nullable @JsonIgnore
    public ZIKStyle getStyle() { return this.style; }

    @Nullable @JsonProperty("properties")
    public Map<String,JsonNode> getProperties() { return this.properties; }

    @Nullable @JsonProperty("links")
    public List<ZIKLink> getLinks() { return this.links; }
    @JsonProperty("links")
    private void setLinks(@Nullable final List<ZIKLink> links) { this.links = links; }

    @Nullable @JsonProperty("actions")
    public List<ZIKTransition> getTransitions() { return this.transitions; }
    @JsonProperty("actions")
    private void setTransitions(@Nullable final List<ZIKTransition> transitions) { this.transitions = transitions; }

    @Nullable @JsonProperty("entities")
    public List<ZIKDevice> getDevices() { return this.devices; }
    @JsonProperty("entities")
    private void setDevices(@Nullable final List<ZIKDevice> devices) { this.devices = devices; }
}
