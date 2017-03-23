package com.easy.adri;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class StringHelpers {

    public static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String apacheConvertStreamToString(InputStream is) throws IOException {
        return IOUtils.toString(is, "utf-8");
    }
}
