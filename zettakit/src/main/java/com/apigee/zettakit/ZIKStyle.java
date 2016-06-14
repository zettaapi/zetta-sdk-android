package com.apigee.zettakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apigee.zettakit.utils.ZIKJsonUtils;

import java.util.Map;

public class ZIKStyle implements Parcelable {
    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String FOREGROUND_COLOR = "foregroundColor";

    @NonNull private final Map<String,Object> properties;
    @NonNull private final Map<String,Object> actions;
    @Nullable private final ZIKStyleColor backgroundColor;
    @Nullable private final ZIKStyleColor foregroundColor;

    @NonNull public Map<String,Object> getProperties() { return this.properties; }
    @NonNull public Map<String,Object> getActions() { return this.actions; }
    @Nullable public ZIKStyleColor getBackgroundColor() { return this.backgroundColor; }
    @Nullable public ZIKStyleColor getForegroundColor() { return this.foregroundColor; }

    @Override
    public String toString() { return ZIKJsonUtils.objectToJsonString(ZIKStyle.class,this); }

    @NonNull
    public ZIKStyle fromString(@NonNull final String string) throws ZIKException {
        return ZIKJsonUtils.createObjectFromJson(ZIKStyle.class,string);
    }

    public ZIKStyle(@NonNull final Map<String,Object> properties, @NonNull final Map<String,Object> actions) {
        this.properties = properties;
        this.actions = actions;
        Object backgroundColorObject = this.properties.get(BACKGROUND_COLOR);
        if( backgroundColorObject != null && backgroundColorObject instanceof Map ) {
            this.backgroundColor = new ZIKStyleColor((Map)backgroundColorObject);
        } else {
            this.backgroundColor = null;
        }
        Object foregroundColorObject = this.properties.get(FOREGROUND_COLOR);
        if( foregroundColorObject != null && foregroundColorObject instanceof Map ) {
            this.foregroundColor = new ZIKStyleColor((Map)foregroundColorObject);
        } else {
            this.foregroundColor = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeString(ZIKJsonUtils.mapToJsonString(this.properties));
        dest.writeString(ZIKJsonUtils.mapToJsonString(this.actions));
        dest.writeParcelable(this.backgroundColor, flags);
        dest.writeParcelable(this.foregroundColor, flags);
    }

    @SuppressWarnings("unchecked")
    protected ZIKStyle(@NonNull final Parcel in) {
        this.properties = ZIKJsonUtils.jsonStringToMap(in.readString());
        this.actions = ZIKJsonUtils.jsonStringToMap(in.readString());
        this.backgroundColor = in.readParcelable(ZIKStyleColor.class.getClassLoader());
        this.foregroundColor = in.readParcelable(ZIKStyleColor.class.getClassLoader());
    }

    public static final Parcelable.Creator<ZIKStyle> CREATOR = new Parcelable.Creator<ZIKStyle>() {
        @Override
        public ZIKStyle createFromParcel(Parcel source) {
            return new ZIKStyle(source);
        }

        @Override
        public ZIKStyle[] newArray(int size) {
            return new ZIKStyle[size];
        }
    };
}
