package com.apigee.zettakit.jsonHelpers;

import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKLink;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public final class ZIKLinkJsonAdapter {
    @FromJson
    public ZIKLink linkFromJson(@NonNull final ZIKLinkJson linkJson) {
        String href = linkJson.href;
        if( href == null ) {
            href = "";
        }
        return new ZIKLink(href,linkJson.title,linkJson.rel);
    }

    @ToJson
    public ZIKLinkJson linkToJson(@NonNull final ZIKLink link) {
        ZIKLinkJson json = new ZIKLinkJson();
        json.href = link.getHref();
        json.title = link.getTitle();
        json.rel = link.getRel();
        return json;
    }
}
