package com.easy.adri;

import android.graphics.Point;
import android.view.View;
import android.view.ViewTreeObserver;

public class ViewHelpers {

    static public int invertVisibility(int visibility) {
        return visibility == View.VISIBLE ? View.GONE : View.VISIBLE;
    }

    static public void setVisibilityNot(View v, int visibility) {
        v.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    public static void whenViewHasLayout(final View v, final JavaHelpers.Callback cb) {

        ViewTreeObserver vto = v.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                cb.call();
            }
        });
    }

    public static Point getPosMiddle(View v) {
        return new Point((int) v.getX() + v.getWidth() / 2, (int) v.getY() + v.getHeight() / 2);
    }
}
