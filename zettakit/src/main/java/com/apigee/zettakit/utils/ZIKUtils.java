package com.apigee.zettakit.utils;

import android.support.annotation.NonNull;

public final class ZIKUtils {
    public static String generateRelForString(@NonNull final String string) {
        return "http://rels.zettajs.io/" + string;
    }
}
