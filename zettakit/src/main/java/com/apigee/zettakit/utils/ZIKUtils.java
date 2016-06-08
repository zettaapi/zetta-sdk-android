package com.apigee.zettakit.utils;

import android.support.annotation.NonNull;

public final class ZIKUtils {
    public static String generateRelForString(@NonNull final String string) {
        return "http://rels.zettajs.io/" + string;
    }

    public static String concatStrings(@NonNull final Iterable<String> strings, @NonNull final String separator) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for(String s: strings) {
            sb.append(sep).append(s);
            sep = separator;
        }
        return sb.toString();
    }
}
