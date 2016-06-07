package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZIKTransition implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.href);
        dest.writeString(this.name);
        dest.writeString(this.method);
        dest.writeString(this.type);
        dest.writeString(ZIKJsonUtils.listToJsonString(this.fields));
    }

    @SuppressWarnings("unchecked")
    protected ZIKTransition(Parcel in) {
        this.href = in.readString();
        this.name = in.readString();
        this.method = in.readString();
        this.type = in.readString();
        List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
        try {
            fields = ZIKJsonUtils.createObjectFromJson(List.class,in.readString());
        } catch (IOException ignored) {}
        this.fields = fields;
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
