package com.apigee.zettakit;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;

public class ZIKServer {

    @Nullable private String name;
    @Nullable private HashMap<String,JsonNode> properties;

    @Nullable private ArrayList<ZIKDevice> devices;
    @Nullable private ArrayList<ZIKLink> links;
    @Nullable private ArrayList<ZIKTransition> transitions;

    @Nullable @JsonIgnore
    public String getName() { return this.name; }

    @Nullable @JsonProperty("properties")
    public HashMap<String,JsonNode> getProperties() { return this.properties; }
    @JsonProperty("properties")
    private void setProperties(@Nullable final HashMap<String,JsonNode> properties) {
        this.properties = properties;
        this.name = null;
        if( properties != null ) {
            final JsonNode nameNode = properties.get("name");
            if( nameNode != null ) {
                this.name = nameNode.asText(null);
            }
        }
    }

    @Nullable @JsonProperty("links")
    public ArrayList<ZIKLink> getLinks() { return this.links; }
    @JsonProperty("links")
    private void setLinks(@Nullable final ArrayList<ZIKLink> links) { this.links = links; }

    @Nullable @JsonProperty("actions")
    public ArrayList<ZIKTransition> getTransitions() { return this.transitions; }
    @JsonProperty("actions")
    private void setTransitions(@Nullable final ArrayList<ZIKTransition> transitions) { this.transitions = transitions; }

    @Nullable @JsonProperty("entities")
    public ArrayList<ZIKDevice> getDevices() { return this.devices; }
    @JsonProperty("entities")
    private void setDevices(@Nullable final ArrayList<ZIKDevice> devices) { this.devices = devices; }
}
