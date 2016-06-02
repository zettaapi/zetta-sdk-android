package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZIKDevice {
    @NonNull private final ZIKDeviceId deviceId;
    @NonNull private final String type;
    @Nullable private final String name;
    @Nullable private final String state;

    @NonNull private final Map<String,JsonNode> properties;
    @Nullable private List<ZIKLink> links;
    @Nullable private List<ZIKLink> streams;
    @Nullable private List<ZIKTransition> transitions;

    public ZIKDevice(@JsonProperty("properties") @NonNull final Map<String,JsonNode> properties) {
        this.properties = properties;
        this.deviceId = new ZIKDeviceId(UUID.fromString(properties.get("id").asText()));
        this.type = properties.get("type").asText();
        final JsonNode nameNode = properties.get("name");
        if( nameNode != null ) {
            this.name = nameNode.asText(null);
        } else {
            this.name = null;
        }
        final JsonNode stateNode = properties.get("state");
        if( stateNode != null ) {
            this.state = stateNode.asText(null);
        } else {
            this.state = null;
        }
    }

    @NonNull @JsonIgnore
    public ZIKDeviceId getDeviceId() { return this.deviceId; }

    @NonNull @JsonIgnore
    public String getType() { return this.type; }

    @Nullable @JsonIgnore
    public String getName() { return this.name; }

    @Nullable @JsonIgnore
    public String getState() { return this.state; }

    @Nullable @JsonIgnore
    public List<ZIKLink> getStreams() { return this.streams; }

    @NonNull @JsonProperty("properties")
    public Map<String, JsonNode> getProperties() { return this.properties; }

    @Nullable @JsonProperty("links")
    public List<ZIKLink> getLinks() { return this.links; }
    @JsonProperty("links")
    private void setLinks(@Nullable final ArrayList<ZIKLink> links) {
        this.links = links;
        if( links != null ) {
            ArrayList<ZIKLink> streams = new ArrayList<>();
            for( ZIKLink link : links ) {
                if( link.hasRel("monitor") ) {
                    streams.add(link);
                }
            }
            if( !streams.isEmpty() ) {
                this.streams = streams;
            }
        }
    }

    @Nullable @JsonProperty("actions")
    public List<ZIKTransition> getTransitions() { return this.transitions; }
    @JsonProperty("actions")
    private void setTransitions(@Nullable final ArrayList<ZIKTransition> transitions) { this.transitions = transitions; }
}
