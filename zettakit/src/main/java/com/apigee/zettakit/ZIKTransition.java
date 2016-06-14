package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.apigee.zettakit.utils.ZIKJsonUtils;
import com.apigee.zettakit.utils.ZIKUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ZIKTransition implements Parcelable {
    private static final String UTF8_CHARSET_NAME = "UTF-8";

    @NonNull private final String href;
    @NonNull private final String name;
    @NonNull private final String method;
    @NonNull private final String type;
    @NonNull private final List<Map<String,Object>> fields;

    @NonNull public String getHref() { return this.href; }
    @NonNull public String getName() { return this.name; }
    @NonNull public String getMethod() { return this.method; }
    @NonNull public List<Map<String,Object>> getFields() { return this.fields; }
    @NonNull public String getType() { return this.type; }

    @Override
    public String toString() { return ZIKJsonUtils.objectToJsonString(ZIKTransition.class,this); }

    public ZIKTransition(@NonNull final String href,
                         @NonNull final String name,
                         @NonNull final String method,
                         @NonNull final String type,
                         @NonNull final List<Map<String,Object>> fields) {
        this.href = href;
        this.name = name;
        this.method = method;
        this.type = type;
        this.fields = fields;
    }

    @NonNull
    public Request requestForTransition(@NonNull final Map<String,Object> args) throws ZIKException {
        Request.Builder requestBuilder = ZIKSession.getSharedSession().requestBuilderWithURL(this.getHref());

        HashMap<String,Object> requestDataMap = new HashMap<>();
        requestDataMap.putAll(args);
        requestDataMap.put("action",this.getName());

        ArrayList<String> encodedParams = new ArrayList<>();
        for( Map.Entry<String,Object> mapEntry : requestDataMap.entrySet() ) {
            try {
                String keyString = URLEncoder.encode(mapEntry.getKey(),UTF8_CHARSET_NAME);
                String valueString = URLEncoder.encode(mapEntry.getValue().toString(),UTF8_CHARSET_NAME);
                encodedParams.add(keyString + "=" + valueString);
            } catch (UnsupportedEncodingException ignored) {}
        }

        try {
            String requestDataString = ZIKUtils.concatStrings(encodedParams,"&");
            requestBuilder.header("Content-Length",Integer.toString(requestDataString.length()));
            requestBuilder.method(this.method, RequestBody.create(MediaType.parse(this.getType()),requestDataString));
            return requestBuilder.build();
        } catch (Exception e) {
            throw new ZIKException(e);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeString(this.href);
        dest.writeString(this.name);
        dest.writeString(this.method);
        dest.writeString(this.type);
        dest.writeString(ZIKJsonUtils.listToJsonString(this.fields));
    }

    @SuppressWarnings("unchecked")
    protected ZIKTransition(@NonNull final Parcel in) {
        this.href = in.readString();
        this.name = in.readString();
        this.method = in.readString();
        this.type = in.readString();
        this.fields = ZIKJsonUtils.jsonStringToList(in.readString());
    }

    public static final Parcelable.Creator<ZIKTransition> CREATOR = new Parcelable.Creator<ZIKTransition>() {
        @Override
        public ZIKTransition createFromParcel(Parcel source) {
            return new ZIKTransition(source);
        }

        @Override
        public ZIKTransition[] newArray(int size) {
            return new ZIKTransition[size];
        }
    };
}
