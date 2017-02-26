package com.easy.adri;

/**
 * Created by adrienmorel on 25/02/2017.
 */

public class StringHelpers {

    public static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
