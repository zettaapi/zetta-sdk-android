package com.apigee.zettakit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;

public class ZIKDevice {
    @NonNull private String uuid;
    @NonNull private String type;
    @Nullable private String name;
    @Nullable private String state;

    @NonNull private HashMap<String,JsonNode> properties;
    @Nullable private ArrayList<ZIKLink> links;
    @Nullable private ArrayList<ZIKLink> streams;
    @Nullable private ArrayList<ZIKTransition> transitions;

    public ZIKDevice(@JsonProperty("properties") @NonNull final HashMap<String,JsonNode> properties) {
        this.properties = properties;
        this.uuid = properties.get("id").asText();
        this.type = properties.get("type").asText();
        final JsonNode nameNode = properties.get("name");
        if( nameNode != null ) {
            this.name = nameNode.asText(null);
        }
        final JsonNode stateNode = properties.get("state");
        if( stateNode != null ) {
            this.state = stateNode.asText(null);
        }
    }

    @NonNull @JsonIgnore
    public String getUuid() { return this.uuid; }

    @NonNull @JsonIgnore
    public String getType() { return this.type; }

    @Nullable @JsonIgnore
    public String getName() { return this.name; }

    @Nullable @JsonIgnore
    public String getState() { return this.state; }

    @Nullable @JsonIgnore
    public ArrayList<ZIKLink> getStreams() { return this.streams; }

    @NonNull @JsonProperty("properties")
    public HashMap<String, JsonNode> getProperties() { return this.properties; }

    @Nullable @JsonProperty("links")
    public ArrayList<ZIKLink> getLinks() { return this.links; }
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
    public ArrayList<ZIKTransition> getTransitions() { return this.transitions; }
    @JsonProperty("actions")
    private void setTransitions(@Nullable final ArrayList<ZIKTransition> transitions) { this.transitions = transitions; }
}
