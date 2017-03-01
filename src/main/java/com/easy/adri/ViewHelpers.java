package com.easy.adri;

import android.view.View;

public class ViewHelpers {

    static public int invertVisibility(int visibility) {
        return visibility == View.VISIBLE ? View.GONE : View.VISIBLE;
    }

    static public void setVisibilityNot(View v, int visibility) {
        v.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
}
